/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.data.test;

import java.util.UUID;

import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlStatement;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Dummy transactional service. Behavior depends on transactional DDL.
 */
public class TestDataService implements TransactionalService {
	Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	public void setup() {
		String schema = session.getSchema();
		new SqlStatement(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schema)).run(session);
		new SqlStatement(String.format("CREATE SCHEMA %s", schema)).run(session);
		new SqlStatement(String.format("CREATE TABLE %s.foo (id varchar(100) PRIMARY KEY, value varchar(100))", schema))
				.run(session);
	}

	public String addValue(String value) {
		String id = UUID.randomUUID().toString();
		int rows = new SqlInsert().table("foo").put("id", id).put("value", value).run(session);
		if (rows != 1) {
			throw new RuntimeException("Insert returned unexpected number of rows: " + rows);
		}
		return id;
	}

	public String getValue(String id) {
		TabularResultSet result = new SqlSelect().table("foo").get("value").where("id = ?", id).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else if (result.rowCount() == 1) {
			return result.row(1).getAsString("value");
		} else {
			throw new RuntimeException("Query returned unexpected number of rows: " + result.rowCount());
		}
	}
	
	public int deleteValue(String value) {
		int rows = new SqlDelete().table("foo").where("value = ?", value).run(session);
		return rows;
	}
}