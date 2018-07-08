/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Checks that invoice has subtotal.
 */
public class InvoiceSubtotalRule extends AbstractRule<Invoice> {
	private static String KEY = "INV-000002-TOTAL";
	private static String SUMMARY = "Invoice subtotal checks";

	public InvoiceSubtotalRule() {
		super(KEY, SUMMARY, ValidationType.INVOICE);
	}

	@Override
	public List<ValidationResult> validate(Invoice invoice, String tenantId) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		ValidationResult result1 = createValidationResult();
		result1.setSummary("Invoice subtotal missing");
		results.add(result1);

		if (invoice.getSubtotalAmount() == null) {
			result1.setDetails("No subtotal found on invoice");
			result1.setPassed(false);
		} else {
			result1.setDetails("Subtotal found on invoice");
			result1.setPassed(true);
		}
		return results;
	}
}