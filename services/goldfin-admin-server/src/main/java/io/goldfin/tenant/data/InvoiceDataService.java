/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.utilities.JsonHelper;

/**
 * Service methods for working with invoices.
 */
public class InvoiceDataService implements TransactionalService<Invoice> {
	static private final Logger logger = LoggerFactory.getLogger(InvoiceDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "document_id", "identifier", "description", "tags",
			"effective_date", "vendor", "subtotal_amount", "tax", "total_amount", "currency", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	public String create(Invoice model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new invoice: " + model.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("invoices").put("id", id).put("document_id", model.getDocumentId())
				.put("identifier", model.getIdentifier()).put("description", model.getDescription())
				.put("tags", model.getTags()).put("effective_date", model.getEffectiveDate())
				.put("vendor", model.getVendor()).put("subtotal_amount", model.getSubtotalAmount())
				.put("tax", model.getTax()).put("total_amount", model.getTotalAmount())
				.put("currency", model.getCurrency()).run(session);
		return id.toString();
	}

	public int update(String id, Invoice model) {
		SqlUpdate update = new SqlUpdate().table("invoices").id(UUID.fromString(id));
		if (model.getDescription() != null) {
			update.put("description", model.getDescription());
		}
		if (model.getTags() != null) {
			update.put("tags", model.getTags());
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("invoices").id(UUID.fromString(id)).run(session);
	}

	public Invoice get(String id) {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES).id(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoice(result.row(1));
		}
	}

	public Invoice getByToken(String token) {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES).where("token = ?", token)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoice(result.row(1));
		}
	}

	public List<Invoice> getAll() {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES).run(session);
		List<Invoice> invoices = new ArrayList<Invoice>(result.rowCount());
		for (Row row : result.rows()) {
			invoices.add(toInvoice(row));
		}
		return invoices;
	}

	private Invoice toInvoice(Row row) {
		Invoice env = new Invoice();
		env.setId(row.getAsUUID("id"));
		env.setDocumentId(row.getAsUUID("document_id"));
		env.setDescription(row.getAsString("description"));
		env.setTags(row.getAsString("tags"));
		env.setIdentifier(row.getAsString("identifier"));
		env.setEffectiveDate(row.getAsDate("effective_date"));
		env.setSubtotalAmount(row.getAsBigDecimal("subtotal_amount"));
		env.setTax(row.getAsBigDecimal("tax"));
		env.setTotalAmount(row.getAsBigDecimal("total_amount"));
		env.setCurrency(row.getAsString("currency"));
		return env;
	}

	private String toJson(Object o) {
		if (o == null) {
			return null;
		} else {
			return JsonHelper.writeToString(o);
		}
	}

	private <T> T fromJson(String s, Class<T> ref) {
		if (s == null) {
			return null;
		} else {
			return (T) JsonHelper.readFromString(s, ref);
		}
	}
}