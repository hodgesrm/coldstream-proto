/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Document.SemanticTypeEnum;
import io.goldfin.admin.service.api.model.Document.StateEnum;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.DocumentDataService;
import io.goldfin.tenant.data.InvoiceDataService;
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
				processInvoiceContent(response);
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

	/**
	 * Handle storage of a scanned invoice.
	 */
	private void processInvoiceContent(StructuredMessage invoiceResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing invoice response: %s", invoiceResponse.toString()));
		String type = invoiceResponse.getType();
		String operation = invoiceResponse.getOperation();
		String tenantId = invoiceResponse.getTenantId();
		String documentId = invoiceResponse.getXactTag();
		if (!"scan".equals(operation)) {
			logger.error("ABORT: Expected a scan operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a scan response");
			return;
		}

		// Decode the invoice and fill in document ID.
		Invoice invoice = invoiceResponse.decodeContent(Invoice.class);
		invoice.setDocumentId(UUID.fromString(documentId));

		// Start a transaction to upsert the invoice, then update the document.
		InvoiceDataService invoiceDataService = new InvoiceDataService();
		DocumentDataService documentDataService = new DocumentDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceDataService).enlist(documentDataService)) {
			// See if we have a document. If not, the transaction must be aborted.
			Document document = documentDataService.get(documentId);
			if (document == null) {
				logger.error(String.format(
						"Document ID does not exist, discarding scanned invoice: tenantId=%s, documentId=%s", tenantId,
						documentId));
				session.rollback();
				return;
			}

			// See if this invoice has already been scanned.
			Invoice existing = invoiceDataService.getByDocumentId(documentId);
			if (existing == null) {
				// Nothing to be done.
			} else {
				// Since the invoice exists, we'll delete it but reuse the old UUID to
				// preserve references.
				logger.info(String.format("Deleting existing invoice: tenantId=%s, invoiceId=%s", tenantId,
						existing.getId().toString()));
				invoiceDataService.delete(existing.getId().toString());
				invoice.setId(existing.getId());
			}

			// Add the new invoice.
			String invoiceId = invoiceDataService.create(invoice);

			// Update the document.
			document.setSemanticType(SemanticTypeEnum.INVOICE);
			document.setSemanticId(UUID.fromString(invoiceId));
			document.setState(StateEnum.SCANNED);
			documentDataService.update(documentId, document);

			// Great, we're done. Commit the whole thing and head for home.
			session.commit();
			logger.info(
					String.format("Successfully stored new scanned invoice: tenantId=%s, invoiceId=%s, documentId=%s",
							tenantId, invoiceId, documentId));
		}
	}
}