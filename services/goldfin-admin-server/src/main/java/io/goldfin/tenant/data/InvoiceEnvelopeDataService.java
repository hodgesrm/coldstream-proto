/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelope.StateEnum;
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
 * Service methods for working with invoice_envelopes.
 */
public class InvoiceEnvelopeDataService implements TransactionalService<InvoiceEnvelope> {
	static private final Logger logger = LoggerFactory.getLogger(InvoiceEnvelopeDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "description", "tags", "state", "source", "ocrscan",
			"creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	public String create(InvoiceEnvelope model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new invoice: " + model.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("invoice_envelopes").put("id", id).put("description", model.getDescription())
				.put("tags", model.getTags()).put("state", model.getState().toString())
				.put("source", toJson(model.getSource()), true).run(session);
		return id.toString();
	}

	public int update(String id, InvoiceEnvelope model) {
		SqlUpdate update = new SqlUpdate().table("invoice_envelopes").id(UUID.fromString(id));
		if (model.getDescription() != null) {
			update.put("description", model.getDescription());
		}
		if (model.getTags() != null) {
			update.put("tags", model.getTags());
		}
		if (model.getState() != null) {
			update.put("state", model.getState().toString());
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("invoice_envelopes").id(UUID.fromString(id)).run(session);
	}

	public InvoiceEnvelope get(String id) {
		TabularResultSet result = new SqlSelect().table("invoice_envelopes").get(COLUMN_NAMES).id(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoiceEnvelope(result.row(1));
		}
	}

	public InvoiceEnvelope getByToken(String token) {
		TabularResultSet result = new SqlSelect().table("invoice_envelopes").get(COLUMN_NAMES).where("token = ?", token)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoiceEnvelope(result.row(1));
		}
	}

	public List<InvoiceEnvelope> getAll() {
		TabularResultSet result = new SqlSelect().table("invoice_envelopes").get(COLUMN_NAMES).run(session);
		List<InvoiceEnvelope> invoiceEnvelopes = new ArrayList<InvoiceEnvelope>(result.rowCount());
		for (Row row : result.rows()) {
			invoiceEnvelopes.add(toInvoiceEnvelope(row));
		}
		return invoiceEnvelopes;
	}

	private InvoiceEnvelope toInvoiceEnvelope(Row row) {
		InvoiceEnvelope env = new InvoiceEnvelope();
		env.setId(row.getAsUUID("id"));
		env.setDescription(row.getAsString("description"));
		env.setTags(row.getAsString("tags"));
		env.setState(StateEnum.fromValue(row.getAsString("state")));
		env.setSource(fromJson(row.getAsString("source"), Document.class));
		env.setCreationDate(row.getAsTimestamp("creation_date").toString());
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