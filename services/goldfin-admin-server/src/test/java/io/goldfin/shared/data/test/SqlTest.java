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
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.DbTestingHelper;

/**
 * Tests SQL operation classes, focusing especially on SELECT statements. 
 */
public class SqlTest {
	static final Logger logger = LoggerFactory.getLogger(SqlTest.class);

	private static final String SCHEMA = "test_sql";
	private static DbConnectionHelper globalConnectionHelper;
	private static DbTestingHelper globalDbHelper;

	/** Ensure schema exists before starting any test. */
	@BeforeClass
	public static void setupAll() throws Exception {
		globalConnectionHelper = new DbConnectionHelper(SCHEMA);
		globalDbHelper = new DbTestingHelper(globalConnectionHelper, SCHEMA);
		globalDbHelper.initializeSchema();
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
		globalDbHelper.createSimpleTestTable(table);
		try (Session s = globalDbHelper.createSession(false)) {
			// Confirm rows are inserted.
			TabularResultSet result = new SqlSelect().from(table).project("id").project(table, "value", "v1").run(s);
			Assert.assertEquals(10, result.rowCount());

			// Update a row to a new value and find it.
			new SqlUpdate().table(table).id(2).put("value", "9999").run(s);
			TabularResultSet result2 = new SqlSelect().from(table).project("id").project("value").where("value = ?", "9999")
					.run(s);
			Assert.assertEquals(1, result2.rowCount());
			Assert.assertEquals(2, result2.row(1).getAsInt(1).intValue());

			// Delete a row and confirm it's gone.
			new SqlDelete().table(table).id(2).run(s);
			TabularResultSet result3 = new SqlSelect().from(table).project("id").project("value").where("value = ?", "9999")
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
		globalDbHelper.createSimpleTestTable(table);
		try (Session s = globalDbHelper.createSession(false)) {
			// Select results with a window function that adds a column with reversed ID
			// values partitioned across true/false values.
			TabularResultSet result = new SqlSelect().from(table).project("id").project("value").project("boolval")
					.projectWindow("w1", "row_number()", "rn").window("w1").partition("boolval").orderByDescending("id")
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

	/**
	 * Verify that we can select from a subquery. 
	 */
	@Test
	public void testSubqueries() {
		String table = "subqueries";
		globalDbHelper.createSimpleTestTable(table);
		try (Session s = globalDbHelper.createSession(false)) {
			// Select results with a window function that adds a column with reversed ID
			// values partitioned across true/false values.
			SqlSelect sub = new SqlSelect().from(table).project("id").project("value").project("boolval")
					.projectWindow("w1", "row_number()", "rn").window("w1").partition("boolval").orderByDescending("id")
					.done().orderByAscending("id");
			TabularResultSet result = new SqlSelect().from(sub, "sub").project("sub.*").run(s);

			// Print values so it's easier to see what the case is doing in case it fails.
			for (Row r : result.rows()) {
				logger.info(String.format("id=%s value=%s boolval=%s rn=%s", r.getAsInt("id"), r.getAsString("value"),
						r.getAsBoolean("boolval"), r.getAsLong("rn")));
			}
			Assert.assertEquals(10, result.rowCount());
			Assert.assertEquals(5, result.row(2).getAsLong("rn").longValue());
		}
	}

	/**
	 * Verify that we can substitute parameters into where clauses including those belonging to subqueries. 
	 */
	@Test
	public void testWhereClauses() {
		String table = "where_clause_1";
		globalDbHelper.createSimpleTestTable(table);
		try (Session s = globalDbHelper.createSession(false)) {
			SqlSelect q1 = new SqlSelect().from(table, "t1").project("t1.*").where("t1.id > ?", 5);
			SqlSelect q2 = new SqlSelect().from(q1, "q1").project("q1.*").where("q1.id < ?", 9);

			// First query should get half the table. 
			TabularResultSet r1 = q1.run(s);
			logResults(r1);
			Assert.assertEquals(5,r1.rowCount());

			// Using same query as a subquery in q2 should net 3 rows. 
			TabularResultSet r2 = q2.run(s);
			logResults(r2);
			Assert.assertEquals(3, r2.rowCount());
		}
	}

	private void logResults(TabularResultSet rs) {
		for (Row r : rs.rows()) {
			logger.info(String.format("id=%s value=%s boolval=%s rn=%s", r.getAsInt("id"), r.getAsString("value"),
					r.getAsBoolean("boolval"), r.getAsLong("rn")));
		}
	}
}