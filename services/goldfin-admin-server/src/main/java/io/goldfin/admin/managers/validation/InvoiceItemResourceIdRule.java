/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceItem;
import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Ensures that each invoice item has a resource ID.
 */
public class InvoiceItemResourceIdRule extends AbstractRule<Invoice> {
	private static String KEY = "INV-000003-RESOURCE-ID";
	private static String SUMMARY = "Invoice Item Resource Id";

	public InvoiceItemResourceIdRule() {
		super(KEY, SUMMARY, ValidationType.INVOICE);
	}

	@Override
	public List<ValidationResult> validate(Invoice invoice, String tenantId) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();

		// Now check that the invoice line item totals sum to the invoice total.
		for (InvoiceItem item : invoice.getItems()) {
			if (item.getResourceId() == null || "".equals(item.getResourceId().trim())) {
				ValidationResult result = createValidationResult();
				result.setSummary("Resource ID missing");
				result.setDetails(String.format("Item description=%s", item.getDescription()));
				result.setItemId(item.getItemId());
				result.setResourceId(null);
				results.add(result);
			}
		}
		return results;
	}
}