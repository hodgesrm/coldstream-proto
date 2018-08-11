/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Service methods for working with API Key data.
 */
public class ApiKeyService implements TransactionalService<ApiKeyData> {
	static private final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

	private static final String[] COLUMN_NAMES = { "id", "user_id", "name", "secret_hash", "algorithm",
			"last_touched_date", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	public String create(ApiKeyData model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new API key: " + model.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("apikeys").put("id", id).put("user_id", model.getUserId()).put("name", model.getName())
				.put("secret_hash", model.getSecretHash()).put("algorithm", model.getAlgorithm())
				.put("last_touched_date", model.getLastTouchedDate()).run(session);
		return id.toString();
	}

	@Override
	public int update(String id, ApiKeyData model) {
		SqlUpdate update = new SqlUpdate().table("apikeys").id(UUID.fromString(id));
		update.put("last_touched_date", model.getLastTouchedDate());
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("apikeys").id(UUID.fromString(id)).run(session);
	}

	public ApiKeyData get(String id) {
		TabularResultSet result = new SqlSelect().from("apikeys").project(COLUMN_NAMES).whereId(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toUserRecord(result.row(1));
		}
	}

	public ApiKeyData getBySecretHash(String secretHash) {
		TabularResultSet result = new SqlSelect().from("apikeys").project(COLUMN_NAMES)
				.where("secret_hash = ?", secretHash).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toUserRecord(result.row(1));
		}
	}

	public List<ApiKeyData> getAll() {
		TabularResultSet result = new SqlSelect().from("apikeys").project(COLUMN_NAMES).run(session);
		List<ApiKeyData> users = new ArrayList<ApiKeyData>(result.rowCount());
		for (Row row : result.rows()) {
			users.add(toUserRecord(row));
		}
		return users;
	}

	public List<ApiKeyData> getAllForUser(String userId) {
		TabularResultSet result = new SqlSelect().from("apikeys").project(COLUMN_NAMES).where("user_id = ?", UUID.fromString(userId))
				.run(session);
		List<ApiKeyData> users = new ArrayList<ApiKeyData>(result.rowCount());
		for (Row row : result.rows()) {
			users.add(toUserRecord(row));
		}
		return users;
	}

	private ApiKeyData toUserRecord(Row row) {
		ApiKeyData apiKey = new ApiKeyData();
		apiKey.setId(row.getAsUUID("id"));
		apiKey.setUserId(row.getAsUUID("user_id"));
		apiKey.setName(row.getAsString("name"));
		apiKey.setSecretHash(row.getAsString("secret_hash"));
		apiKey.setAlgorithm(row.getAsString("algorithm"));
		apiKey.setLastTouchedDate(row.getAsTimestamp("last_touched_date"));
		apiKey.setCreationDate(row.getAsTimestamp("creation_date"));
		return apiKey;
	}
}