/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.shared.cloud.AwsConnectionParams;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;

/**
 * Handles operations related to OCR, specifically initiating scans on documents
 * and applying the resulting content to the appropriate document type. (For now
 * that's just invoices.)
 */
public class OcrManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(OcrManager.class);
	private ManagementContext context;

	// Names of our queues. 
	private String ocrRequestQueue;
	private String ocrResponseQueue;

	// Thread pool to read OCR responses.
	private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	/**
	 * Ensure that queues used for OCR exist.
	 */
	@Override
	public void prepare() {
		// Ensure the queues exist.
		ocrRequestQueue = ensureQueue("ocrRequestQueue");
		ocrResponseQueue = ensureQueue("ocrResponseQueue");

		// Start the response queue thread.
		OcrResponseQueueTask task = new OcrResponseQueueTask(context, ocrResponseQueue);
		threadPool.submit(task);
	}

	private String ensureQueue(String propName) {
		// Get the queue location.
		AwsConnectionParams awsParams = context.getAwsConnectionParams();
		String queueName = awsParams.getOcr().getProperty(propName);
		if (queueName == null) {
			throw new RuntimeException(String.format("Queue property value missing: name=%s", propName));
		}

		// Ensure the queue exists.
		QueueConnection queue = CloudConnectionFactory.getInstance().getQueueConnection(queueName);
		if (queue.queueExists()) {
			logger.info(String.format("OCR queue exists: %s", queueName));
		} else {
			logger.info(String.format("OCR queue does not exist, creating: %s", queueName));
			queue.queueCreate(false);
		}
		return queueName;
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	/** 
	 * Submit a document for OCR scanning. 
	 * @param tenantId Tenant ID
	 * @param document Document record
	 */
	public void scan(String tenantId, Document document) {
		// Generate a request to scan the document.
		QueueConnection conn = CloudConnectionFactory.getInstance().getQueueConnection(ocrRequestQueue);
		logger.info(String.format("Requesting document scan: tenantId=%s, documentId=%s, name=%s", tenantId,
				document.getId().toString(), document.getName()));
		StructuredMessage request = new StructuredMessage().setOperation("scan").setType("request")
				.setXactTag(document.getId().toString()).setTenantId(tenantId).encodeContent(document);
		conn.send(request);
		logger.info(String.format("Document scan request enqueued: tenantId=%s, documentId=%s, name=%s", tenantId,
				document.getId().toString(), document.getName()));
	}
}