/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.svc;

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
 * Service methods for working with sessions.
 */
public class SessionDataService implements TransactionalService<SessionData> {
	static private final Logger logger = LoggerFactory.getLogger(SessionDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "user_id", "token", "last_touched_date", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	public String create(SessionData model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new session: " + model.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("sessions").put("id", id).put("user_id", model.getUserId()).put("token", model.getToken())
				.run(session);
		return id.toString();
	}

	public int update(String id, SessionData model) {
		SqlUpdate update = new SqlUpdate().table("sessions").id(UUID.fromString(id));
		update.put("last_touched_date", model.getLastTouched());
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("sessions").id(UUID.fromString(id)).run(session);
	}

	public SessionData get(String id) {
		TabularResultSet result = new SqlSelect().from("sessions").project(COLUMN_NAMES).whereId(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toSessionRecord(result.row(1));
		}
	}

	public SessionData getByToken(String token) {
		TabularResultSet result = new SqlSelect().from("sessions").project(COLUMN_NAMES)
				.where("token = ?", token).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toSessionRecord(result.row(1));
		}
	}

	public List<SessionData> getAll() {
		TabularResultSet result = new SqlSelect().from("sessions").project(COLUMN_NAMES).run(session);
		List<SessionData> sessions = new ArrayList<SessionData>(result.rowCount());
		for (Row row : result.rows()) {
			sessions.add(toSessionRecord(row));
		}
		return sessions;
	}

	private SessionData toSessionRecord(Row row) {
		SessionData session = new SessionData();
		session.setId(row.getAsUUID("id"));
		session.setUserId(row.getAsUUID("user_id"));
		session.setToken(row.getAsString("token"));
		session.setLastTouched(row.getAsTimestamp("last_touched_date"));
		session.setCreationDate(row.getAsTimestamp("creation_date"));
		return session;
	}
}