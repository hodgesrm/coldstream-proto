/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
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

	public void addScannedInvoice(String tenantId, Invoice invoice) {
		// If there is an existing invoice for this document ID, we need to delete it. 

		// Add the new invoice. 
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
			Invoice invoice = invoiceDataService.getComplete(id);
			if (invoice == null) {
				throw new EntityNotFoundException("Invoice does not exist");
			}
			return invoice;
		}
	}

	public List<Invoice> getAllInvoices(Principal principal, Boolean full) {
		String tenantId = getTenantId(principal);
		InvoiceDataService invoiceService = new InvoiceDataService();
		try (Session session = context.tenantSession(tenantId).enlist(invoiceService)) {
			if (full == null || ! full) {
				return invoiceService.getAll();
			}
			else {
				return invoiceService.getAllComplete();
			}
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