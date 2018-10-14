/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.extract.ExtractFactory;
import io.goldfin.shared.extract.ExtractQuery;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.DbTestingHelper;

/**
 * Tests generation of CSV from result sets.
 */
public class ExtractTest {
	static final Logger logger = LoggerFactory.getLogger(ExtractTest.class);

	private static final String SCHEMA = "test_extract";
	private static DbConnectionHelper globalConnectionHelper;
	private static DbTestingHelper globalDbHelper;

	/** Ensure schema exists before starting any test. */
	@BeforeClass
	public static void setupAll() throws Exception {
		// Set up the schema for extract testing.
		globalConnectionHelper = new DbConnectionHelper(SCHEMA);
		globalDbHelper = new DbTestingHelper(globalConnectionHelper, SCHEMA);
		globalDbHelper.initializeSchema();
		globalDbHelper.createSimpleTestTable("extract_table");

		// Add the sample extract we'll use for unit testing.
		ExtractFactory.getInstance().addExtract(new SampleExtract());
	}

	/**
	 * Verify that we can return the extract values normally if there are no
	 * additional parameters.
	 */
	@Test
	public void testExtractSimple() throws IOException {
		try (Session s = globalDbHelper.createSession(false)) {
			ExtractQuery eq = new ExtractQuery().name("sample_extract");
			SqlSelect query = eq.build(s);
			logger.info(query.build());
			TabularResultSet result = query.run(s);
			result.logResults(logger);

			// Should have 10 rows in with IDs sorted in order 1 to 10.
			Assert.assertEquals(10, result.rowCount());
			Assert.assertEquals(Integer.valueOf(1), result.row(1).getAsInt("id"));
			Assert.assertEquals(Integer.valueOf(10), result.row(10).getAsInt("id"));
		}
	}

	/**
	 * Verify that we can return the extract values normally if there are no
	 * additional parameters.
	 */
	@Test
	public void testExtractSorted() throws IOException {
		try (Session s = globalDbHelper.createSession(false)) {
			ExtractQuery eq1 = new ExtractQuery().name("sample_extract").sortOrder("boolval:ASC,id:DESC");
			ExtractQuery eq2 = new ExtractQuery().name("sample_extract").sortOrder("boolval,id:DESC");
			ExtractQuery[] queries = new ExtractQuery[] { eq1, eq2 };

			// ASC is a default so both queries should give the same result.
			for (ExtractQuery eq : queries) {
				SqlSelect query = eq.build(s);
				logger.info(query.build());
				TabularResultSet result = query.run(s);
				result.logResults(logger);

				// Should have 10 rows in with IDs as follows: odd numbers first in descending
				// order, followed by evens.
				Assert.assertEquals(10, result.rowCount());
				Assert.assertEquals(Integer.valueOf(9), result.row(1).getAsInt("id"));
				Assert.assertEquals(Integer.valueOf(2), result.row(10).getAsInt("id"));
			}
		}
	}

	/**
	 * Verify that limit reduces rows from extract.
	 */
	@Test
	public void testExtractLimit() throws IOException {
		int[] limits = { 1, 5, 10 };
		try (Session s = globalDbHelper.createSession(false)) {
			for (int limit : limits) {
				ExtractQuery eq = new ExtractQuery().name("sample_extract").limit(limit);
				TabularResultSet result = eq.build(s).run(s);
				result.logResults(logger);
				Assert.assertEquals(limit, result.rowCount());
			}
		}
	}

	/**
	 * Verify that we detect invalid extract specifications including bad extract
	 * name, invalid sort specification format, non-existent columns.
	 */
	@Test
	public void testExtractErrors() throws Exception {
		try (Session s = globalDbHelper.createSession(false)) {
			// Invalid extract name.
			logger.info("Bad extract name");
			try {
				new ExtractQuery().name("does not exist");
				throw new Exception("Allowed invalid name");
			} catch (InvalidInputException e) {
				logger.info("Expected exception: " + e.getMessage());
			}

			// Various types of invalid sort order specification.
			logger.info("Invalid sort specification");
			String[] badSortSpecs = { "id:FFF", "id:ASC:something", "value:DESC,id:bad", ":ASC", };
			for (String spec : badSortSpecs) {
				ExtractQuery eq = new ExtractQuery().name("sample_extract");
				try {
					eq.sortOrder(spec);
					throw new Exception("Allowed invalid sort spec: " + spec);
				} catch (InvalidInputException e) {
					logger.info("Expected exception: " + e.getMessage());
				}
			}

			// Invalid column names for sorts.
			logger.info("Invalid sort columns");
			String[] badSortColumns = { "id1:ASC", "(id):DESC", "bad_function(id)" };
			for (String spec : badSortColumns) {
				ExtractQuery eq = new ExtractQuery().name("sample_extract").sortOrder(spec);
				try {
					eq.build(s);
					throw new Exception("Allowed invalid sort column: " + spec);
				} catch (InvalidInputException e) {
					logger.info("Expected exception: " + e.getMessage());
				}
			}
		}
	}
}