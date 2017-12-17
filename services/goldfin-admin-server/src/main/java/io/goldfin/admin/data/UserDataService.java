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
 * Service methods for working with user data.
 */
public class UserDataService implements TransactionalService<UserData> {
	static private final Logger logger = LoggerFactory.getLogger(UserDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "tenant_id", "username", "roles", "password_hash", "algorithm",
			"creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	public String create(UserData model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new user: " + model.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("users").put("id", id).put("tenant_id", model.getTenantId())
				.put("username", model.getUsername()).put("roles", model.getRoles())
				.put("password_hash", model.getPasswordHash()).put("algorithm", model.getAlgorithm()).run(session);
		return id.toString();
	}

	public int update(String id, UserData model) {
		SqlUpdate update = new SqlUpdate().table("users").id(UUID.fromString(id));
		boolean paramsAdded = false;

		if (model.getUsername() != null) {
			update.put("username", model.getUsername());
			paramsAdded = true;
		}
		if (model.getRoles() != null) {
			update.put("roles", model.getRoles());
			paramsAdded = true;
		}
		if (model.getPasswordHash() != null) {
			update.put("password_hash", model.getPasswordHash());
			paramsAdded = true;
		}
		if (model.getAlgorithm() != null) {
			update.put("algorithm", model.getAlgorithm());
			paramsAdded = true;
		}
		if (!paramsAdded) {
			return 0;
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("users").id(UUID.fromString(id)).run(session);
	}

	public UserData get(String id) {
		TabularResultSet result = new SqlSelect().table("users").get(COLUMN_NAMES).id(UUID.fromString(id)).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toUserRecord(result.row(1));
		}
	}

	public UserData getByUsername(String username) {
		TabularResultSet result = new SqlSelect().table("users").get(COLUMN_NAMES).where("username = ?", username)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toUserRecord(result.row(1));
		}
	}

	public List<UserData> getAll() {
		TabularResultSet result = new SqlSelect().table("users").get(COLUMN_NAMES).run(session);
		List<UserData> users = new ArrayList<UserData>(result.rowCount());
		for (Row row : result.rows()) {
			users.add(toUserRecord(row));
		}
		return users;
	}

	private UserData toUserRecord(Row row) {
		UserData userRecord = new UserData();
		userRecord.setId(row.getAsUUID("id"));
		userRecord.setTenantId(row.getAsUUID("tenant_id"));
		userRecord.setUsername(row.getAsString("username"));
		userRecord.setRoles(row.getAsString("roles"));
		userRecord.setPasswordHash(row.getAsString("password_hash"));
		userRecord.setAlgorithm(row.getAsString("algorithm"));
		userRecord.setCreationDate(row.getAsTimestamp("creation_date"));
		return userRecord;
	}
}