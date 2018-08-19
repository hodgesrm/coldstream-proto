/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Document.SemanticTypeEnum;
import io.goldfin.admin.service.api.model.Document.StateEnum;
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
public class DocumentDataService implements TransactionalService<Document> {
	static private final Logger logger = LoggerFactory.getLogger(DocumentDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "name", "description", "tags", "content_type",
			"content_length", "thumbprint", "locator", "state", "semantic_type", "semantic_id", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	public String create(Document model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new document: " + model.toString());
		}
		UUID id;
		if (model.getId() == null) {
			id = UUID.randomUUID();
		} else {
			id = model.getId();
		}
		new SqlInsert().table("documents").put("id", id).put("name", model.getName())
				.put("description", model.getDescription())
				.put("tags", JsonHelper.writeToStringOrNull(model.getTags()), true)
				.put("content_type", model.getContentType()).put("content_length", model.getContentLength())
				.put("thumbprint", model.getThumbprint()).put("locator", model.getLocator())
				.put("state", stringOrNull(model.getState()))
				.put("semantic_type", stringOrNull(model.getSemanticType())).put("semantic_id", model.getSemanticId())
				.run(session);
		return id.toString();
	}

	private String stringOrNull(Object o) {
		if (o == null) {
			return null;
		} else {
			return o.toString();
		}
	}

	public int update(String id, Document model) {
		SqlUpdate update = new SqlUpdate().table("documents").id(UUID.fromString(id));
		if (model.getDescription() != null) {
			update.put("description", model.getDescription());
		}
		if (model.getTags() != null) {
			update.put("tags", JsonHelper.writeToStringOrNull(model.getTags()), true);
		}
		if (model.getState() != null) {
			update.put("state", stringOrNull(model.getState()));
		}
		if (model.getSemanticType() != null) {
			update.put("semantic_type", stringOrNull(model.getSemanticType()));
		}
		if (model.getSemanticId() != null) {
			update.put("semantic_id", model.getSemanticId());
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("documents").id(UUID.fromString(id)).run(session);
	}

	public Document get(String id) {
		TabularResultSet result = new SqlSelect().from("documents").project(COLUMN_NAMES).whereId(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toDocument(result.row(1));
		}
	}

	public Document getByThumbprint(String thumbprint) {
		TabularResultSet result = new SqlSelect().from("documents").project(COLUMN_NAMES)
				.where("thumbprint = ?", thumbprint).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toDocument(result.row(1));
		}
	}

	public List<Document> getAll() {
		TabularResultSet result = new SqlSelect().from("documents").project(COLUMN_NAMES).run(session);
		List<Document> invoiceEnvelopes = new ArrayList<Document>(result.rowCount());
		for (Row row : result.rows()) {
			invoiceEnvelopes.add(toDocument(row));
		}
		return invoiceEnvelopes;
	}

	private Document toDocument(Row row) {
		Document doc = new Document();
		doc.setId(row.getAsUUID("id"));
		doc.setName(row.getAsString("name"));
		doc.setDescription(row.getAsString("description"));
		doc.setContentType(row.getAsString("content_type"));
		doc.setContentLength(row.getAsBigDecimal("content_length"));
		doc.setThumbprint(row.getAsString("thumbprint"));
		doc.setLocator(row.getAsString("locator"));
		doc.setState(StateEnum.fromValue(row.getAsString("state")));
		doc.setSemanticType(SemanticTypeEnum.fromValue(row.getAsString("semantic_type")));
		doc.setSemanticId(row.getAsUUID("semantic_id"));
		doc.setTags(JsonHelper.readFromStringOrNull(row.getAsString("tags"), ExtendedTagSet.class));
		doc.setCreationDate(row.getAsTimestamp("creation_date").toString());
		return doc;
	}
}