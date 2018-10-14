/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import io.goldfin.shared.data.SqlSelect;

/**
 * Defines an extract to select invoice headers joined to matching invoice
 * items.
 */
public class InvoiceItemExtract implements Extract {

	public String name() {
		return "invoice_item";
	}

	/** Return the base query. */
	public SqlSelect baseQuery() {
		return new SqlSelect().from("invoices", "invoice")
				.innerJoin("invoice_items", "item", "invoice.id", "item.invoice_id").project("*")
				.orderByAscending("invoice.identifier").orderByAscending("invoice.effective_date")
				.orderByAscending("item.rid");
	}
}