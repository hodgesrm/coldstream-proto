/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import io.goldfin.shared.data.SqlSelect;

/**
 * Defines an extract to select invoice headers.
 */
public class InvoiceExtract implements Extract {

	public String name() {
		return "invoice";
	}

	public SqlSelect baseQuery() {
		return new SqlSelect().from("invoices", "i").project("i.*").orderByAscending("effective_date")
				.orderByAscending("vendor");
	}
}