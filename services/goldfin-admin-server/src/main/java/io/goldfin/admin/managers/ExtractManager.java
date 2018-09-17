/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.io.StringWriter;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.extract.CsvBuilder;

/**
 * Handles operations related to data series, which are uploads of observations.
 */
public class ExtractManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(ExtractManager.class);
	private ManagementContext context;

	// Extract names.
	private static String INVOICE = "invoice";
	private static String INVOICE_ITEM = "invoice_item";
	// private static String HOST = "host";

	// Output types.
	public enum OutputTypes {
		CSV
	}

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

	}

	/** Return extract in form ready for download. */
	public String getExtract(Principal principal, String extractType, String filter, OutputTypes output)
			throws Exception {
		String tenantId = getTenantId(principal);
		SqlSelect select = null;
		if (INVOICE.equals(extractType.toLowerCase())) {
			select = new SqlSelect().from("invoices", "i").project("i.*");
		} else if (INVOICE_ITEM.equals(extractType.toLowerCase())) {
			select = new SqlSelect().from("invoices", "invoice")
					.innerJoin("invoice_items", "item", "invoice.id", "item.invoice_id").project("*")
					.orderByAscending("invoice.identifier").orderByAscending("invoice.effective_date")
					.orderByAscending("item.item_row_number").orderByAscending("item_row_number");
		} else {
			throw new InvalidInputException("Unknown extract type: " + extractType);
		}

		try (Session session = context.tenantSession(tenantId)) {
			TabularResultSet resultSet = select.run(session);
			StringWriter sw = new StringWriter();
			new CsvBuilder().resultSet(resultSet).writer(sw).write();
			String csv = sw.toString();
			return csv;
		} catch (Exception e) {
			logger.error("Query failed", e);
			throw new Exception("Query failed");
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