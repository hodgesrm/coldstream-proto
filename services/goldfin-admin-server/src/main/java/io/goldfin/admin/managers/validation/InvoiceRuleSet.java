/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.service.api.model.Invoice;

/**
 * Contains rules related to invoices. Rules are returned in order of
 * definition.
 */
public class InvoiceRuleSet {
	private static List<Rule<Invoice>> invoiceRules = new ArrayList<Rule<Invoice>>();

	private static void addInvoiceRule(Rule<Invoice> rule) {
		invoiceRules.add(rule);
	}

	// Add in all known rules. This need only be done a single time as rules are
	// immutable.
	static {
		addInvoiceRule(new InvoiceTotalRule());
		addInvoiceRule(new InvoiceToHostInventoryRule());
	}

	/** Return rules related to invoices. */
	public List<Rule<Invoice>> getInvoiceRules() {
		return invoiceRules;
	}
}