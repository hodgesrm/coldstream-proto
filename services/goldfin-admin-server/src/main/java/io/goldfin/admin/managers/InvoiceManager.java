/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.security.Principal;
import java.util.List;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.InvoiceDataService;

/**
 * Handles operations related to invoices.
 */
public class InvoiceManager implements Manager {
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

	public void deleteInvoice(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		InvoiceDataService invoiceEnvelopeService = new InvoiceDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceEnvelopeService)) {
			int rows = invoiceEnvelopeService.delete(id);
			session.commit();
			if (rows == 0) {
				throw new EntityNotFoundException("Invoice does not exist");
			}
		}
	}

	public Invoice getInvoice(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		InvoiceDataService invoiceDataService = new InvoiceDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceDataService)) {
			Invoice invoiceEnvelope = invoiceDataService.get(id);
			if (invoiceEnvelope == null) {
				throw new EntityNotFoundException("Invoice does not exist");
			}
			return invoiceEnvelope;
		}
	}

	public List<Invoice> getAllInvoices(Principal principal) {
		String tenantId = getTenantId(principal);
		InvoiceDataService invoiceEnvelopService = new InvoiceDataService();
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