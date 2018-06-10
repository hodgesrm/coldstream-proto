/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlStatement;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.testing.DbConnectionHelper;

/**
 * Tests SQL operation classes.
 */
public class SqlTest {
	static final Logger logger = LoggerFactory.getLogger(SqlTest.class);

	private static final String SCHEMA = "test_sql";
	private static DbConnectionHelper globalConnectionHelper;

	/** Ensure schema exists before starting any test. */
	@BeforeClass
	public static void setupAll() throws Exception {
		globalConnectionHelper = new DbConnectionHelper(SCHEMA);
		try (Session sess = createSession(false)) {
			new SqlStatement(String.format("DROP SCHEMA IF EXISTS %s CASCADE", SCHEMA)).run(sess);
			new SqlStatement(String.format("CREATE SCHEMA %s", SCHEMA)).run(sess);
		}
	}

	@Before
	public void setup() {
		// pass
	}

	/**
	 * Verify that we can implement happy-path CRUD operations on a simple table.
	 */
	@Test
	public void testBasicCrudOperations() {
		String table = "basic_crud_operations";
		createSimpleTestTable(table);
		try (Session s = createSession(false)) {
			// Confirm rows are inserted.
			TabularResultSet result = new SqlSelect().from(table).get("id").get(table, "value", "v1").run(s);
			Assert.assertEquals(10, result.rowCount());

			// Update a row to a new value and find it.
			new SqlUpdate().table(table).id(2).put("value", "9999").run(s);
			TabularResultSet result2 = new SqlSelect().from(table).get("id").get("value").where("value = ?", "9999")
					.run(s);
			Assert.assertEquals(1, result2.rowCount());
			Assert.assertEquals(2, result2.row(1).getAsInt(1).intValue());

			// Delete a row and confirm it's gone.
			new SqlDelete().table(table).id(2).run(s);
			TabularResultSet result3 = new SqlSelect().from(table).get("id").get("value").where("value = ?", "9999")
					.run(s);
			Assert.assertEquals(0, result3.rowCount());
		}
	}

	/**
	 * Verify that we can apply windowing functions to select additional named
	 * columns.
	 */
	@Test
	public void testWindowFunctions() {
		String table = "window_functions";
		createSimpleTestTable(table);
		try (Session s = createSession(false)) {
			// Select results with a window function that adds a column with reversed ID
			// values partitioned across true/false values.
			TabularResultSet result = new SqlSelect().from(table).get("id").get("value").get("boolval")
					.getWindow("w1", "row_number()", "rn").window("w1").partition("boolval").orderByDescending("id")
					.done().orderByAscending("id").run(s);
			// Print values so it's easier to see what the case is doing in case it fails.
			for (Row r : result.rows()) {
				logger.info(String.format("id=%s value=%s boolval=%s rn=%s", r.getAsInt("id"), r.getAsString("value"),
						r.getAsBoolean("boolval"), r.getAsLong("rn")));
			}
			Assert.assertEquals(10, result.rowCount());
			Assert.assertEquals(5, result.row(2).getAsLong("rn").longValue());
		}
	}

	/** Create a session and ensure that the schema exists. */
	private static Session createSession(boolean transactional) {
		Session session = new SessionBuilder().connectionManager(globalConnectionHelper.getConnectionManager())
				.ensureSchema(globalConnectionHelper.getSchema()).transactional(transactional).build();
		return session;
	}

	/** Create a simple test table with 10 rows. */
	private void createSimpleTestTable(String name) {
		try (Session session = createSession(false)) {
			new SqlStatement(String.format(
					"CREATE TABLE %s.%s (id int PRIMARY KEY, value varchar(100), boolval boolean)", SCHEMA, name))
							.run(session);
			for (int i = 1; i <= 10; i++) {
				new SqlInsert().table(name).put("id", i).put("value", new Integer(i).toString())
						.put("boolval", new Boolean(i % 2 == 0)).run(session);
			}
		}
	}
}