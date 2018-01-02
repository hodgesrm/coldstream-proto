/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelope.StateEnum;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.crypto.Sha256HashingAlgorithm;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.InvoiceEnvelopeDataService;

/**
 * Handles operations related to tenants.
 */
public class InvoiceManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(InvoiceManager.class);
	private ManagementContext context;

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	@Override
	public void prepare() {
		// Do nothing for now.
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	public InvoiceEnvelope createInvoiceEnvelope(Principal principal, InputStream content, String fileName,
			String description) throws IOException {
		String tenantId = getTenantId(principal);
		InvoiceEnvelopeDataService invoiceEnvelopeService = new InvoiceEnvelopeDataService();

		// Read the file and create a document structure for it.
		long length = 0;
		while (content.read() >= 0) {
			length++;
		}
		logger.info(String.format("Read file, length=%d", length));

		// Create a dummy document reference.
		Document document = new Document();
		document.setId(UUID.randomUUID().toString());
		document.setName(fileName);
		document.setLocator(
				String.format("https://s3.amazon.com/%s/%s", UUID.randomUUID().toString(), document.getId()));
		document.setThumbprint(Sha256HashingAlgorithm.generateHashString(document.getId()));
		document.setCreationDate(new Timestamp(System.currentTimeMillis()).toString());

		// Build the model.
		InvoiceEnvelope invoice = new InvoiceEnvelope();
		invoice.setDescription(description);
		invoice.setState(StateEnum.CREATED);
		invoice.setSource(document);

		// Store invoice data.
		logger.info("Creating new invoice, fileName=" + fileName);
		String id;
		try (Session session = context.tenantSession(tenantId).enlist(invoiceEnvelopeService)) {
			id = invoiceEnvelopeService.create(invoice);
			session.commit();
		}
		logger.info("Invoice created: id=" + tenantId);

		// All done!
		return this.getInvoiceEnvelope(principal, id);
	}

	public void deleteInvoiceEnvelope(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		InvoiceEnvelopeDataService invoiceEnvelopeService = new InvoiceEnvelopeDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceEnvelopeService)) {
			int rows = invoiceEnvelopeService.delete(id);
			session.commit();
			if (rows == 0) {
				throw new EntityNotFoundException("InvoiceEnvelope does not exist");
			}
		}
	}

	public InvoiceEnvelope getInvoiceEnvelope(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		InvoiceEnvelopeDataService invoiceEnvelopeService = new InvoiceEnvelopeDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceEnvelopeService)) {
			InvoiceEnvelope invoiceEnvelope = invoiceEnvelopeService.get(id);
			if (invoiceEnvelope == null) {
				throw new EntityNotFoundException("InvoiceEnvelope does not exist");
			}
			return invoiceEnvelope;
		}
	}

	public List<InvoiceEnvelope> getAllInvoiceEnvelopes(Principal principal) {
		String tenantId = getTenantId(principal);
		InvoiceEnvelopeDataService invoiceEnvelopService = new InvoiceEnvelopeDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceEnvelopService)) {
			return invoiceEnvelopService.getAll();
		}
	}

	/** Get the tenant ID for this user. */
	private String getTenantId(Principal principal) {
		UserManager userManager = ManagerRegistry.getInstance().getManager(UserManager.class);
		User user = userManager.getUser(principal.getName());
		if (user == null) {
			throw new RuntimeException(String.format("Unknown user: %s", principal.getName()));
		} else {
			return user.getTenantId().toString();
		}
	}
}