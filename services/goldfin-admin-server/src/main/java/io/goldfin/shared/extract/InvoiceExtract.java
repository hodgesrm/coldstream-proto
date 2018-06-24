/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.data.SqlSelect;

/**
 * Defines an extract to select invoice headers. 
 */
public class InvoiceExtract extends ExtractDefinition {

	public InvoiceExtract() {
		super("invoice", Invoice.class);
	}

	/** Return the base query. */
	public SqlSelect baseQuery() {
		return new SqlSelect().from("invoices", "i").project("i.*");
	}
}