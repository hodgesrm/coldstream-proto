/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract.test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.extract.CsvBuilder;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.DbTestingHelper;

/**
 * Tests generation of CSV from result sets.
 */
public class CsvBuilderTest {
	static final Logger logger = LoggerFactory.getLogger(CsvBuilderTest.class);

	private static final String SCHEMA = "test_extract";
	private static DbConnectionHelper globalConnectionHelper;
	private static DbTestingHelper globalDbHelper;

	/** Ensure schema exists before starting any test. */
	@BeforeClass
	public static void setupAll() throws Exception {
		globalConnectionHelper = new DbConnectionHelper(SCHEMA);
		globalDbHelper = new DbTestingHelper(globalConnectionHelper, SCHEMA);
		globalDbHelper.initializeSchema();
	}

	/**
	 * Verify that we can extract data from a simple table into CSV.
	 */
	@Test
	public void testSimpleExtract() throws IOException {
		String table = "simple_extract";
		globalDbHelper.createSimpleTestTable(table);
		try (Session s = globalDbHelper.createSession(false)) {
			TabularResultSet result = new SqlSelect().from(table, "t").project("t.*").run(s);
			Writer csvOutput = new StringWriter();
			new CsvBuilder().resultSet(result).writer(csvOutput).write();
			String csv = csvOutput.toString();
			logger.info(String.format("CSV: \n%s", csv));
		}
	}

	/**
	 * Verify that we can extract data from a table containing all data types.
	 */
	@Test
	public void testAllTypesExtract() throws IOException {
		String table = "all_types_extract";
		globalDbHelper.createTableOfManyTypes(table);
		try (Session s = globalDbHelper.createSession(false)) {
			TabularResultSet result = new SqlSelect().from(table, "t").project("t.*").run(s);
			Writer csvOutput = new StringWriter();
			new CsvBuilder().resultSet(result).writer(csvOutput).write();
			String csv = csvOutput.toString();
			logger.info(String.format("CSV: \n%s", csv));
		}
	}
}