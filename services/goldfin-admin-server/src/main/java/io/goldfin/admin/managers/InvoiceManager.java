/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelopeParameters;
import io.goldfin.admin.service.api.model.User;
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

	public InvoiceEnvelope createInvoiceEnvelope(String tenantId, InvoiceEnvelopeParameters model) {
		return null;
	}

	public void deleteInvoiceEnvelope(String tenantId, String id) {
	}

	public InvoiceEnvelope getInvoiceEnvelope(String tenantId, String id) {
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