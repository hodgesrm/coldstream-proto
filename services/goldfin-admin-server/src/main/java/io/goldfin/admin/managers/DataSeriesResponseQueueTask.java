/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Document.StateEnum;
import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.DocumentDataService;

/**
 * Executes an endless loop to look for and process data series analysis responses.
 */
public class DataSeriesResponseQueueTask implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(DataSeriesResponseQueueTask.class);

	private final ManagementContext context;
	private final String dataSeriesResponseQueue;

	public DataSeriesResponseQueueTask(ManagementContext context, String dataSeriesResponseQueue) {
		this.context = context;
		this.dataSeriesResponseQueue = dataSeriesResponseQueue;
	}

	/**
	 * Read from queue indefinitely.
	 */
	@Override
	public void run() {
		try {
			// Open up a queue connection.
			logger.info("Opening connection to response queue");
			QueueConnection queue = CloudConnectionFactory.getInstance().getQueueConnection(dataSeriesResponseQueue);

			// Read from the queue until we are interrupted.
			long livenessMillis = System.currentTimeMillis() + 60000;
			long successful = 0;
			long errors = 0;
			while (!Thread.currentThread().isInterrupted()) {
				// Periodically show we're alive.
				if (System.currentTimeMillis() > livenessMillis) {
					logger.info(String.format("Data series response handler liveness check: successful=%d, errors=%d",
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
			if ("Host".equals(response.getContentClass())) {
				processContent(response);
			} else if ("ApiResponse".equals(response.getContentClass())) {
				ApiResponseMessage apiResponse = response.decodeContent(ApiResponseMessage.class);
				logger.error("Got an API response back: " + apiResponse.toString());
				if ("error".equalsIgnoreCase(apiResponse.getType())) {
					processScanError(response);
				}
			} else {
				logger.error(String.format("Unexpected content class: class=%s, content=%s", response.getContentClass(),
						response.getContent()));
			}
			return true;
		}
	}

	/**
	 * Handle storage of inventory data.
	 */
	private void processContent(StructuredMessage inventoryResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing invoice response: %s", inventoryResponse.toString()));
		String type = inventoryResponse.getType();
		String operation = inventoryResponse.getOperation();
		String tenantId = inventoryResponse.getTenantId();
		String documentId = inventoryResponse.getXactTag();
		if (!"process".equals(operation)) {
			logger.error("ABORT: Expected a process operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a process response");
			return;
		}

		// Decode the invoice and fill in document ID.
		Host[] hosts = inventoryResponse.decodeContent(new Host[0].getClass());
		throw new RuntimeException("Unimplemented");

		// Start a transaction to upsert host inventory records, then update the data series record. 
	}

	/**
	 * Handle a document scan error
	 */
	private void processScanError(StructuredMessage inventoryResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing inventory response: %s", inventoryResponse.toString()));
		String type = inventoryResponse.getType();
		String operation = inventoryResponse.getOperation();
		String tenantId = inventoryResponse.getTenantId();
		String documentId = inventoryResponse.getXactTag();
		if (!"process".equals(operation)) {
			logger.error("ABORT: Expected a process operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a process response");
			return;
		}

		// Decode the response message.
		ApiResponseMessage apiResponse = inventoryResponse.decodeContent(ApiResponseMessage.class);

		// Start a transaction to update the document with ERROR state.
		DocumentDataService documentDataService = new DocumentDataService();
		try (Session session = context.tenantSession(tenantId).enlist(documentDataService)) {
			// See if we have a document. If not, the transaction must be aborted.
			Document document = documentDataService.get(documentId);
			if (document == null) {
				logger.error(String.format(
						"Document ID does not exist, discarding error response: tenantId=%s, documentId=%s", tenantId,
						documentId));
				session.rollback();
				return;
			}

			// Update the document.
			document.setState(StateEnum.ERROR);
			documentDataService.update(documentId, document);

			// Commit.
			session.commit();
			logger.info(String.format("Logged document scan error: tenantId=%s, documentId=%s, message=%s", tenantId,
					documentId, apiResponse.getMessage()));
		}
	}
}