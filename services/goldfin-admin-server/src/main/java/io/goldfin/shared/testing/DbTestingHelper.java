/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.testing;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlStatement;

/**
 * Helper class for SQL testing. Starts sessions and creates sessions, etc.
 */
public class DbTestingHelper {
	static final Logger logger = LoggerFactory.getLogger(DbTestingHelper.class);

	private final DbConnectionHelper connectionHelper;
	private final String schema;

	public DbTestingHelper(DbConnectionHelper connectionHelper, String schema) {
		this.connectionHelper = connectionHelper;
		this.schema = schema;
	}

	/** Initialize DB schema. */
	public void initializeSchema() throws Exception {
		try (Session sess = createSession(false)) {
			new SqlStatement(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schema)).run(sess);
			new SqlStatement(String.format("CREATE SCHEMA %s", schema)).run(sess);
		}
	}

	/** Create a session. */
	public Session createSession(boolean transactional) {
		Session session = new SessionBuilder().connectionManager(connectionHelper.getConnectionManager())
				.ensureSchema(connectionHelper.getSchema()).transactional(transactional).build();
		return session;
	}

	/** Create a simple test table with 10 rows. */
	public void createSimpleTestTable(String name) {
		try (Session session = createSession(false)) {
			new SqlStatement(String.format(
					"CREATE TABLE %s.%s (id int PRIMARY KEY, value varchar(100), boolval boolean)", schema, name))
							.run(session);
			for (int i = 1; i <= 10; i++) {
				new SqlInsert().table(name).put("id", i).put("value", new Integer(i).toString())
						.put("boolval", new Boolean(i % 2 == 0)).run(session);
			}
		}
	}

	/** Create a table with columns of every data type. */
	public void createTableOfManyTypes(String name) {
		try (Session session = createSession(false)) {
			String sqlFormat = "CREATE TABLE %s.%s(" + "my_boolean boolean, " + "my_bigint bigint, "
					+ "my_char char(3), " + "my_float float, " + "my_integer integer, " + "my_numeric numeric(10,2), "
					+ "my_timestamp timestamp, " + "my_uuid uuid, " + "my_varchar varchar(10)" + ")";
			new SqlStatement(String.format(sqlFormat, schema, name)).run(session);

			long baseMillis = System.currentTimeMillis();
			String letters = "abcdefghijklmnopqrstuvwxyz";
			for (int i = 1; i <= 10; i++) {
				Boolean bool = (i % 2 == 0);
				Float floatVal = i * (float) 1.5;
				Float numericVal = i * (float) 1.5;
				Integer smallInt = i;
				Integer largeInt = i * 1000000;
				Timestamp timestamp = new Timestamp(baseMillis + (i * 60000));
				String shortString = letters.substring(i, i + 3);
				String longString = letters.substring(i, i + 10);

				new SqlInsert().table(name).put("my_boolean", bool).put("my_bigint", largeInt)
						.put("my_char", shortString).put("my_float", floatVal).put("my_integer", smallInt)
						.put("my_numeric", numericVal).put("my_timestamp", timestamp).put("my_uuid", UUID.randomUUID())
						.put("my_varchar", longString).run(session);
			}
		}

	}
}