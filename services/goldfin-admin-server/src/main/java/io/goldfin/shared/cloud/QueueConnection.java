/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.util.List;
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
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import io.goldfin.shared.utilities.JsonHelper;

/**
 * Implements encapsulated operations on a single SQS queue.
 */
public class QueueConnection {
	static final Logger logger = LoggerFactory.getLogger(QueueConnection.class);

	private final AwsConnectionParams connectionParams;
	private final String queue;

	/**
	 * Auto-closable wrapper for Amazon SQS client to enable operations to allocate
	 * and free resources using a try block.
	 */
	class SqsClientWrapper implements AutoCloseable {
		private final AmazonSQS client;

		SqsClientWrapper(AwsConnectionParams params) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(params.getAccessKeyId(),
					params.getSecretAccessKey());
			AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
			client = AmazonSQSClientBuilder.standard().withCredentials(provider).build();
		}

		public AmazonSQS getConnection() {
			return client;
		}

		@Override
		public void close() {
			client.shutdown();
		}
	}

	public QueueConnection(AwsConnectionParams connectionParams, String queue) {
		this.connectionParams = connectionParams;
		this.queue = queue;
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
	 * Send a JSON message to the queue.
	 * 
	 * @param request
	 *            Object that will be serialized to JSON
	 */
	public void sendJsonMessage(Object o) {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				String message = JsonHelper.writeToString(o);
				SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
						.withMessageBody(message).withDelaySeconds(0);
				sqs.sendMessage(send_msg_request);
			} catch (AmazonServiceException e) {
				this.handleException(String.format("Queue creation failed: name=%s, message=%s", queue, e.getMessage()),
						e);
			}
		}
	}

	/**
	 * Read a JSON message from the queue.
	 * 
	 * @param objectType
	 *            Type into which JSON message is deserialized
	 */
	public <T> QueueResponse<T> receiveJsonMessage(Class<T> objectType) {
		QueueResponse<T> jsonMessage = null;
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
				if (messages.size() > 0) {
					Message m1 = messages.get(0);
					if (logger.isDebugEnabled()) {
						logger.debug(toDebugMessage(m1));
					}
					T object = JsonHelper.readFromString(m1.getBody(), objectType);
					jsonMessage = new QueueResponse<T>(m1.getReceiptHandle(), object);
				}

				// delete messages from the queue
				for (Message m : messages) {
					sqs.deleteMessage(queueUrl, m.getReceiptHandle());
				}
			} catch (AmazonServiceException e) {
				handleException(String.format("Message receive failed: name=%s, message=%s", queue, e.getMessage()), e);
			}
		}
		return jsonMessage;
	}

	/**
	 * Delete a message from a queue.
	 * 
	 * @param key
	 *            Key used for deletion
	 */
	public void deleteMessage(String key) {
		try (SqsClientWrapper wrapper = new SqsClientWrapper(connectionParams)) {
			final AmazonSQS sqs = wrapper.getConnection();
			try {
				String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
				sqs.deleteMessage(queueUrl, key);
			} catch (AmazonServiceException e) {
				handleException(String.format("Queue creation failed: name=%s, message=%s", queue, e.getMessage()), e);
			}
		}
	}

	private String toDebugMessage(Message m) {
		StringBuffer sb = new StringBuffer();
		sb.append("Received message:");
		sb.append(String.format("\n  MessageId: %s", m.getMessageId()));
		sb.append(String.format("\n  ReceiptHandle: %s", m.getReceiptHandle()));
		sb.append(String.format("\n  MD5OfBody: %s", m.getMD5OfBody()));
		sb.append(String.format("\n  Body: %s", m.getBody()));
		for (Entry<String, String> entry : m.getAttributes().entrySet()) {
			sb.append(String.format("\n  Attribute: %s, Value: %s", entry.getKey(), entry.getValue()));
		}
		return sb.toString();
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