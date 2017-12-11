/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.dbutils.test;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.utilities.FileHelper;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Verifies loading of SQL scripts. Test DBMS connection parameters must be
 * stored in src/test/resources/dbms.yaml.
 */
public class SqlScriptExecutorTest {
	static final Logger logger = LoggerFactory.getLogger(SqlScriptExecutorTest.class);

	private ConnectionParams testDbParams;

	/** Load DBMS connection parameters. */
	@Before
	public void setup() throws Exception {
		testDbParams = YamlHelper.readFromClasspath("dbms.yaml", ConnectionParams.class);
	}

	/**
	 * Verify that we can load a script with parameter substitutions. 
	 */
	@Test
	public void testScriptLoading() throws Exception {
		File testDir = FileHelper.resetDirectory(new File("target/testdata/testScriptLoading"));
		File testFile = FileHelper.writeLines(new File(testDir, "load.sql"), "create schema if not exists {{schema}}", ";", 
				"drop schema if exists {{schema}}", ";");
		Properties props = new Properties();
		props.setProperty("schema", "test_schema");
		
		// Execute the script. 
		SqlScriptExecutor executor = new SqlScriptExecutor(testDbParams, props);
		executor.execute(testFile);
	}
}