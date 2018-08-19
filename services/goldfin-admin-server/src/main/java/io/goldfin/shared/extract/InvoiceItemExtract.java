/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import io.goldfin.admin.data.tenant.InvoiceDataService;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.data.SqlSelect;

/**
 * Defines an extract to select invoice headers joined to matching invoice
 * items.
 */
public class InvoiceItemExtract extends ExtractDefinition {

	public InvoiceItemExtract() {
		super("invoice", Invoice.class);
	}

	/** Return the base query. */
	public SqlSelect baseQuery() {
		// Needs a join...
		return new SqlSelect().from("invoice_items").project(InvoiceDataService.COLUMN_NAMES_ITEMS)
				.orderByAscending("invoice_id").orderByAscending("item_row_number");
	}
}