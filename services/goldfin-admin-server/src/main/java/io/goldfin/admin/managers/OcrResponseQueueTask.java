/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.swagger.annotations.ApiResponse;

/**
 * Executes an endless loop to look for and process OCR responses.
 */
public class OcrResponseQueueTask implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(OcrResponseQueueTask.class);

	private final ManagementContext context;
	private final String ocrResponseQueue;

	public OcrResponseQueueTask(ManagementContext context, String ocrResponseQueue) {
		this.context = context;
		this.ocrResponseQueue = ocrResponseQueue;
	}

	/**
	 * Read from queue indefinitely.
	 */
	@Override
	public void run() {
		try {
			// Open up a queue connection.
			logger.info("Opening connection to response queue");
			QueueConnection queue = CloudConnectionFactory.getInstance().getQueueConnection(ocrResponseQueue);

			// Read from the queue until we are interrupted.
			long livenessMillis = System.currentTimeMillis() + 60000;
			long successful = 0;
			long errors = 0;
			while (!Thread.currentThread().isInterrupted()) {
				// Periodically show we're alive.
				if (System.currentTimeMillis() > livenessMillis) {
					logger.info(String.format("OCR response handler liveness check: successful=%d, errors=%d",
							successful, errors));
					livenessMillis = System.currentTimeMillis() + 60000;
				}
				// Try to read a message.
				try {
					if (this.readFromQueue(queue)) {
						successful++;
					}
				} catch (Exception e) {
					logger.error("Queue read operation failed", e);
					errors++;
				}
			}
		} catch (Throwable t) {
			logger.error("Response handler failed", t);
		}
	}

	/**
	 * Read and process message from queue.
	 * 
	 * @param queue
	 *            SQS queue
	 * @return True if a message was read, otherwise false
	 */
	private boolean readFromQueue(QueueConnection queue) {
		// Look for a message.
		StructuredMessage response = queue.receive(true);
		if (response == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return false;
		} else {
			// We have one now, so let's try to process it.
			if (logger.isDebugEnabled()) {
				logger.debug("Received message: " + response.toString());
			}
			if ("Invoice".equals(response.getContentClass())) {
				Invoice invoice = response.decodeContent(Invoice.class);
				logger.info("Got an invoice back: " + invoice.toString());
			} else if ("ApiResponse".equals(response.getContentClass())) {
				ApiResponse apiResponse = response.decodeContent(ApiResponse.class);
				logger.info("Got an API response back: " + apiResponse.toString());
			} else {
				logger.error(String.format("Unexpected content class: class=%s, content=%s", response.getContentClass(),
						response.getContent()));
			}
			return true;
		}
	}
}