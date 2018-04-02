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
import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.DocumentDataService;
import io.goldfin.tenant.data.InvoiceDataService;
import io.goldfin.tenant.data.VendorDataService;

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
		if (logger.isDebugEnabled()) {
			logger.debug(invoice.toString());
		}

		// Start a transaction to upsert the invoice, then update the document.
		InvoiceDataService invoiceDataService = new InvoiceDataService();
		DocumentDataService documentDataService = new DocumentDataService();
		VendorDataService vendorDataService = new VendorDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceDataService).enlist(documentDataService)
				.enlist(vendorDataService)) {
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

			// See if the vendor exists. If not create one now.
			if (invoice.getVendor() == null) {
				logger.warn(
						String.format("No vendor on invoice, unable to validate vendor record: tenant=%s, invoice=%s",
								tenantId, invoiceId));
			} else {
				Vendor vendor = vendorDataService.getByIdentifier(invoice.getVendor());
				if (vendor == null) {
					logger.info(String.format("Adding new vendor for tenant: tenant=%s, invoice=%s, vendor=%s",
							tenantId, invoiceId, invoice.getVendor()));
					vendor = new Vendor();
					vendor.setIdentifier(invoice.getVendor());
					vendor.setName(invoice.getVendor());
					vendor.setState(Vendor.StateEnum.ACTIVE);
					vendorDataService.create(vendor);
				}
			}

			// Great, we're done. Commit the whole thing and head for home.
			session.commit();
			logger.info(
					String.format("Successfully stored new scanned invoice: tenantId=%s, invoiceId=%s, documentId=%s",
							tenantId, invoiceId, documentId));
		}
	}

	/**
	 * Handle a document scan error
	 */
	private void processScanError(StructuredMessage scanResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing invoice response: %s", scanResponse.toString()));
		String type = scanResponse.getType();
		String operation = scanResponse.getOperation();
		String tenantId = scanResponse.getTenantId();
		String documentId = scanResponse.getXactTag();
		if (!"scan".equals(operation)) {
			logger.error("ABORT: Expected a scan operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a scan response");
			return;
		}

		// Decode the response message.
		ApiResponseMessage apiResponse = scanResponse.decodeContent(ApiResponseMessage.class);

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