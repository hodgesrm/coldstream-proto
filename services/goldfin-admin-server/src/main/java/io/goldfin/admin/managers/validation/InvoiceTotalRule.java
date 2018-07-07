/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceItem;
import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Checks that invoice items sum to the total.
 */
public class InvoiceTotalRule extends AbstractRule<Invoice> {
	private static String KEY = "INV-000001-TOTAL";
	private static String SUMMARY = "Invoice total checks";

	public InvoiceTotalRule() {
		super(KEY, SUMMARY, ValidationType.INVOICE);
	}

	@Override
	public List<ValidationResult> validate(Invoice invoice, String tenantId) {
		// Ensure that the invoice has a visible total. If not that's already a problem.
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		ValidationResult result1 = createValidationResult();
		result1.setSummary("Invoice total missing");
		results.add(result1);

		if (invoice.getTotalAmount() == null) {
			result1.setDetails("No total found on invoice");
			result1.setPassed(false);
			return results;
		} else {
			result1.setDetails("Total found on invoice");
			result1.setPassed(true);
		}

		// Now check that the invoice line item totals sum to the invoice total.
		ValidationResult result2 = createValidationResult();
		result2.setSummary("Invoice total does not match item totals");
		results.add(result2);
		BigDecimal itemTotalAmount = new BigDecimal(0);
		for (InvoiceItem item : invoice.getItems()) {
			if (item.getTotalAmount() != null) {
				itemTotalAmount = itemTotalAmount.add(item.getTotalAmount());
			}
		}
		if (itemTotalAmount.equals(invoice.getTotalAmount())) {
			result2.setDetails(String.format("Totals match: invoice total=%s, item total=%s",
					invoice.getTotalAmount().toString(), itemTotalAmount.toString()));
			result2.setPassed(true);
		} else {
			result2.setDetails(String.format("Totals do not match: invoice total=%s, item total=%s",
					invoice.getTotalAmount().toString(), itemTotalAmount.toString()));
			result2.setPassed(false);
		}
		return results;
	}
}