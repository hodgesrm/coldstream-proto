/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Implements encapsulated operations on a single SQS queue.
 */
public class QueueConnection {
	static final Logger logger = LoggerFactory.getLogger(QueueConnection.class);

	// Connection parameters.
	private final AwsParams connectionParams;
	private final String queue;

	/**
	 * Auto-closable wrapper for Amazon SQS client to enable operations to allocate
	 * and free resources using a try block.
	 */
	class SqsClientWrapper implements AutoCloseable {
		private final AmazonSQS client;

		SqsClientWrapper(AwsParams params) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(params.getAccessKeyId(),
					params.getSecretAccessKey());
			AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
			client = AmazonSQSClientBuilder.standard().withCredentials(provider).withRegion(params.getRegion()).build();
		}

		public AmazonSQS getConnection() {
			return client;
		}

		@Override
		public void close() {
			client.shutdown();
		}
	}

	/**
	 * Starts a new queue connection.
	 * 
	 * @param connectionParams
	 *            Amazon connection parameters.
	 * @param queueHandle
	 *            A short form of the queue name that is unique within the service
	 *            and serves as the basis of the full queue name.
	 */
	public QueueConnection(AwsParams connectionParams, String queueHandle) {
		this.connectionParams = connectionParams;
		this.queue = String.format("%s-%s", connectionParams.getGroup(), queueHandle);
	}

	/**
	 * Create the queue.
	 * 
	 * @param ignoreErrorIfExists
	 *            If true, ignore error if the queue exists.
	 */
	public void queueCreate(boolean ignoreErrorIfExists) {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				sqs.createQueue(queue);
			} catch (AmazonSQSException e) {
				if (ignoreErrorIfExists && e.getErrorCode().equals("QueueAlreadyExists")) {
					// pass
					throw e;
				} else {
					this.handleException(
							String.format("Queue creation failed: name=%s, message=%s", queue, e.getMessage()), e);
				}

			}
		}
	}

	/**
	 * Delete the queue. This succeeds even if the queue does not exist.
	 */
	public void queueDelete() {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				sqs.deleteQueue(queueUrl);
			} catch (QueueDoesNotExistException e) {
				// Pass
				logger.debug(String.format("Queue does not exist: %s", queue));
			} catch (AmazonSQSException e) {
				this.handleException(String.format("Queue deletion failed: name=%s, message=%s", queue, e.getMessage()),
						e);
			}
		}
	}

	/**
	 * Returns true if the queue exists.
	 */
	public boolean queueExists() {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				GetQueueAttributesRequest request = new GetQueueAttributesRequest().withQueueUrl(queueUrl);
				sqs.getQueueAttributes(request);
				return true;
			} catch (AmazonSQSException e) {
				return false;
			}
		}
	}

	/**
	 * Send a structured message to the queue.
	 * 
	 * @param message
	 *            Message contents
	 */
	public void send(StructuredMessage message) {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
						.withMessageAttributes(toMessageAttributes(message.getHeaders()))
						.withMessageBody(message.getContent()).withDelaySeconds(0);
				if (logger.isDebugEnabled()) {
					logger.debug(toDebugSendMessageRequest(send_msg_request));
				}
				sqs.sendMessage(send_msg_request);
			} catch (AmazonServiceException e) {
				this.handleException(String.format("Queue creation failed: name=%s, message=%s", queue, e.getMessage()),
						e);
			}
		}
	}

	/**
	 * Read a message from the queue.
	 * 
	 * @param objectType
	 *            Type into which JSON message is deserialized
	 * @return Structue
	 */
	public StructuredMessage receive(boolean deleteOnReceipt) {
		StructuredMessage message = null;
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				ReceiveMessageRequest receiveParams = new ReceiveMessageRequest().withQueueUrl(queueUrl)
						.withAttributeNames("All").withMessageAttributeNames("All");
				List<Message> messages = sqs.receiveMessage(receiveParams).getMessages();
				if (messages.size() > 0) {
					Message m1 = messages.get(0);
					if (logger.isDebugEnabled()) {
						logger.debug(toDebugMessage(m1));
					}
					message = new StructuredMessage().setHeaders(fromMessageAttributes(m1.getMessageAttributes()))
							.setContent(m1.getBody()).setReceiptHandle(m1.getReceiptHandle());
					if (deleteOnReceipt) {
						sqs.deleteMessage(queueUrl, message.getReceiptHandle());
					}
				}
			} catch (AmazonServiceException e) {
				handleException(String.format("Message receive failed: name=%s, message=%s", queue, e.getMessage()), e);
			}
		}
		return message;
	}

	/**
	 * Delete a message from a queue.
	 * 
	 * @param key
	 *            Key used for deletion
	 */
	public void deleteMessage(StructuredMessage message) {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				sqs.deleteMessage(queueUrl, message.getReceiptHandle());
			} catch (AmazonServiceException e) {
				handleException(String.format("Queue creation failed: name=%s, message=%s", queue, e.getMessage()), e);
			}
		}
	}

	private Map<String, MessageAttributeValue> toMessageAttributes(Map<String, String> headers) {
		HashMap<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
		for (String key : headers.keySet()) {
			MessageAttributeValue mav = new MessageAttributeValue();
			mav.setStringValue(headers.get(key));
			mav.setDataType("String");
			attributes.put(key, mav);
		}
		return attributes;
	}

	private Map<String, String> fromMessageAttributes(Map<String, MessageAttributeValue> attributes) {
		HashMap<String, String> headers = new HashMap<String, String>();
		for (String key : attributes.keySet()) {
			MessageAttributeValue mav = attributes.get(key);
			if ("Number".equals(mav.getDataType())) {
				headers.put(key, mav.getStringValue());
			} else if ("String".equals(mav.getDataType())) {
				headers.put(key, mav.getStringValue());
			} else {
				throw new RuntimeException(String.format(
						"Message attributes must be string or number type: key=%s, type=%s", key, mav.getDataType()));
			}
		}
		return headers;
	}

	private String toDebugSendMessageRequest(SendMessageRequest smr) {
		StringBuffer sb = new StringBuffer();
		sb.append("Sending message:");
		sb.append(String.format("\n  Body: %s", summaryContent(smr.getMessageBody(), 200)));
		for (Entry<String, MessageAttributeValue> entry : smr.getMessageAttributes().entrySet()) {
			sb.append(String.format("\n  Attribute: %s, Type: %s, Value: %s", entry.getKey(),
					entry.getValue().getDataType(), entry.getValue().getStringValue()));
		}
		return sb.toString();
	}

	private String toDebugMessage(Message m) {
		StringBuffer sb = new StringBuffer();
		sb.append("Received message:");
		sb.append(String.format("\n  MessageId: %s", m.getMessageId()));
		sb.append(String.format("\n  ReceiptHandle: %s", m.getReceiptHandle()));
		sb.append(String.format("\n  MD5OfBody: %s", m.getMD5OfBody()));
		sb.append(String.format("\n  Body: %s", summaryContent(m.getBody(), 200)));
		for (Entry<String, String> entry : m.getAttributes().entrySet()) {
			sb.append(String.format("\n  Attribute: %s, Value: %s", entry.getKey(), entry.getValue()));
		}
		for (Entry<String, MessageAttributeValue> entry : m.getMessageAttributes().entrySet()) {
			sb.append(String.format("\n  Message Attribute: %s, Type: %s, Value: %s", entry.getKey(),
					entry.getValue().getDataType(), entry.getValue().getStringValue()));
		}
		return sb.toString();
	}

	private String summaryContent(String s, int len) {
		if (s.length() <= len) {
			return s;
		} else {
			return s.substring(0, len - 1) + "...";
		}
	}

	private void handleException(String message, AmazonServiceException e) {
		logger.error(message, e);
		logger.error("Error Message: " + e.getMessage());
		logger.error("HTTP Status Code: " + e.getStatusCode());
		logger.error("AWS Error Code: " + e.getErrorCode());
		logger.error("Error Type: " + e.getErrorType());
		logger.error("Request ID: " + e.getRequestId());
		throw new RuntimeException(message, e);
	}
}