/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.data.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlStatement;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Dummy transactional service. Behavior depends on transactional DDL.
 */
public class TestDataService implements TransactionalService<SampleEntity> {
	private Session session;
	private static String[] COLUMN_NAMES = { "id", "value" };

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	public void setup() {
		String schema = session.getSchema();
		new SqlStatement(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schema)).run(session);
		new SqlStatement(String.format("CREATE SCHEMA %s", schema)).run(session);
		new SqlStatement(String.format("CREATE TABLE %s.foo (id varchar(100) PRIMARY KEY, value varchar(100))", schema))
				.run(session);
	}

	@Override
	public String create(SampleEntity model) {
		String id = UUID.randomUUID().toString();
		int rows = new SqlInsert().table("foo").put("id", id).put("value", model.getValue()).run(session);
		if (rows != 1) {
			throw new RuntimeException("Insert returned unexpected number of rows: " + rows);
		}
		return id;
	}

	@Override
	public int update(String id, SampleEntity model) {
		SqlUpdate update = new SqlUpdate().table("foo").id(id);
		boolean paramsAdded = false;
		if (model.getValue() != null) {
			update.put("value", model.getValue());
			paramsAdded = true;
		}
		if (!paramsAdded) {
			return 0;
		}
		return update.run(session);
	}

	@Override
	public int delete(String id) {
		int rows = new SqlDelete().table("foo").id(id).run(session);
		return rows;
	}

	public int deleteByValue(String value) {
		int rows = new SqlDelete().table("foo").where("value = ?", value).run(session);
		return rows;
	}

	@Override
	public SampleEntity get(String id) {
		TabularResultSet result = new SqlSelect().table("foo").get(COLUMN_NAMES).where("id = ?", id).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else if (result.rowCount() == 1) {
			return toSampleEntity(result.row(1));
		} else {
			throw new RuntimeException("Query returned unexpected number of rows: " + result.rowCount());
		}
	}

	@Override
	public List<SampleEntity> getAll() {
		TabularResultSet result = new SqlSelect().table("foo").get(COLUMN_NAMES).run(session);
		List<SampleEntity> entities = new ArrayList<SampleEntity>(result.rowCount());
		for (Row row : result.rows()) {
			entities.add(toSampleEntity(row));
		}
		return entities;
	}

	private SampleEntity toSampleEntity(Row r) {
		SampleEntity se = new SampleEntity();
		se.setId(r.getAsString("id"));
		se.setValue(r.getAsString("value"));
		return se;
	}
}