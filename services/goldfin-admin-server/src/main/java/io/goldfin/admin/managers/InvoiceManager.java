/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.managers.validation.InvoiceRuleSet;
import io.goldfin.admin.managers.validation.Rule;
import io.goldfin.admin.managers.validation.ValidationResult;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceValidationResult;
import io.goldfin.admin.service.api.model.InvoiceValidationResult.ValidationTypeEnum;
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
			if (full == null || !full) {
				return invoiceService.getAll();
			} else {
				return invoiceService.getAllComplete();
			}
		}
	}

	public List<InvoiceValidationResult> validate(Principal principal, String id, boolean onlyFailing) {
		String tenantId = getTenantId(principal);

		// Find the invoice in question.
		InvoiceDataService invoiceDataService = new InvoiceDataService();
		Invoice invoice = null;
		try (Session session = context.tenantSession(tenantId).enlist(invoiceDataService)) {
			invoice = invoiceDataService.getComplete(id);
			if (invoice == null) {
				throw new EntityNotFoundException("Invoice does not exist");
			}
		}

		// Apply invoice-level rules.
		InvoiceRuleSet ruleSet = new InvoiceRuleSet();
		List<InvoiceValidationResult> results = new ArrayList<InvoiceValidationResult>();
		for (Rule<Invoice> rule : ruleSet.getInvoiceRules()) {
			for (ValidationResult result : rule.validate(invoice)) {
				if (onlyFailing && !result.isPassed()) {
					results.add(translateResult(invoice, result));
				} else {
					results.add(translateResult(invoice, result));
				}
			}
		}

		return results;
	}

	private InvoiceValidationResult translateResult(Invoice invoice, ValidationResult result) {
		InvoiceValidationResult invResult = new InvoiceValidationResult();
		invResult.setSummary(result.getSummary());
		invResult.setPassed(result.isPassed());
		ValidationTypeEnum validationEnum = ValidationTypeEnum.fromValue(result.getValidationType().name());
		invResult.setValidationType(validationEnum);
		invResult.setDetails(result.getDetails());
		invResult.setInvoiceId(invoice.getId());
		invResult.setIdentifier(invoice.getIdentifier());
		invResult.setEffectiveDate(invoice.getEffectiveDate());
		invResult.setVendorIdentifier(invoice.getVendorIdentifier());
		return invResult;
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