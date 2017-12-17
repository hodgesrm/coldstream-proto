/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.dbutils.test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.dbutils.SqlBatch;
import io.goldfin.shared.dbutils.SqlScript;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Verifies SQL script handling.
 */
public class SqlScriptTest {
	static final Logger logger = LoggerFactory.getLogger(SqlScriptTest.class);

	/**
	 * Verify that we can load a SQL script, apply parameter translations, and
	 * convert to a list of batches. This includes single and multiple substitutions
	 * per line.
	 */
	@Test
	public void testBasicScript() throws Exception {
		File testDir = FileHelper.resetDirectory(new File("target/testdata/testBasicSqlScript"));
		File testFile = FileHelper.writeLines(new File(testDir, "script.sql"),
				// Batch with a comment.
				"// A comment", "{{a}} 1", ";",
				// Batch with single substitution.
				"select {{b}}", ";",
				// Batch with multiple substitutions on one line.
				"select {{b}}, '{{a}}', 2", ";",
				// Batch with multiple substitutions across lines.
				"select '{{a}}',", "{{b}}, 3", ";");
		Properties props = new Properties();
		props.setProperty("a", "select");
		props.setProperty("b", "1");
		List<SqlBatch> batches = new SqlScript(testFile, props).getBatches();

		// Test the batches.
		Assert.assertEquals(4, batches.size());
		Assert.assertEquals("select 1", batches.get(0).getContent());
		Assert.assertEquals("select 1", batches.get(1).getContent());
		Assert.assertEquals("select 1, 'select', 2", batches.get(2).getContent());
		Assert.assertEquals("select 'select', 1, 3", batches.get(3).getContent());
	}

	/**
	 * Verify that empty batches of various types do not generate unexpected
	 * content.
	 */
	@Test
	public void testEmptyBatches() throws Exception {
		File testDir = FileHelper.resetDirectory(new File("target/testdata/testEmptyBatches"));
		File testFile = FileHelper.writeLines(new File(testDir, "script2.sql"), "// a comment", ";", "    ", ";",
				" {{a}} ", ";");
		Properties props = new Properties();
		props.setProperty("a", "select");
		List<SqlBatch> batches = new SqlScript(testFile, props).getBatches();
		Assert.assertEquals(1, batches.size());
		Assert.assertEquals("select", batches.get(0).getContent());
	}

	/**
	 * Verify that invalid parameter substitutions generate errors.
	 */
	@Test
	public void testScriptErrors() throws Exception {
		File testDir = FileHelper.resetDirectory(new File("target/testdata/testScriptErrors"));
		String[] errors = { ";", "{{non-terminated-name", "{{non-terminated-name} ", "select {{non-existent}} from t",
				"//non-terminated batch here!" };
		Properties props = new Properties();
		props.setProperty("a", "select");

		int scriptNo = 0;
		for (String error : errors) {
			// First string is OK just to prove that those that follow generate errors.
			try {
				File testFile = FileHelper.writeLines(new File(testDir, "script" + scriptNo + ".sql"), "// a comment",
						"select 1", error);
				List<SqlBatch> batches = new SqlScript(testFile, props).getBatches();
				Assert.assertEquals(1, batches.size());
				Assert.assertEquals(";", error);
			} catch (Exception e) {
				Assert.assertNotEquals(";", error);
				logger.info(String.format("Caught expected error: case=[%s], message=[%s]", error, e.getMessage()));
			}
		}
	}
}