/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.dbutils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Processor for a single parameterized SQL script.
 */
public class SqlScript {
	private static final String COMMENT = "//";
	private static final String DELIMITER = ";";
	private static final String DIRECTIVE = "//!";
	private static final String DIR_NON_TRANSACTIONAL = "NON-TRANSACTIONAL";
	private final File scriptFile;
	private final Properties parameters;

	private List<SqlBatch> batches;
	private boolean transactional;

	public SqlScript(File scriptFile, Properties parameters) throws SqlLoadException {
		this.scriptFile = scriptFile;
		this.parameters = parameters;
		load();
	}

	public List<SqlBatch> getBatches() {
		return batches;
	}

	public boolean isTransactional() {
		return transactional;
	}

	/**
	 * Read script and substitute parameters.
	 */
	private void load() throws SqlLoadException {
		try {
			// Read file and perform substitutions.
			List<String> rawLines = Files.readAllLines(Paths.get(scriptFile.getCanonicalPath()),
					Charset.defaultCharset());
			List<String> parameterizedLines = new ArrayList<String>(rawLines.size());
			transactional = true;
			int lineno = 0;
			for (String line : rawLines) {
				lineno++;
				// Substitute all variables on the line.
				while (line.indexOf("{{") > -1) {
					int leftBraces = line.indexOf("{{");
					int rightBraces = line.indexOf("}}");
					if (rightBraces > leftBraces) {
						String left = line.substring(0, leftBraces);
						String variable = line.substring(leftBraces + 2, rightBraces);
						String right = line.substring(rightBraces + 2, line.length());
						if (variable.length() == 0) {
							throw new SqlLoadException(
									String.format("Braces do not contain variable name: line=%d, source=%s", lineno,
											scriptFile.getAbsolutePath()));
						}
						String value = parameters.getProperty(variable);
						if (value == null) {
							throw new SqlLoadException(
									String.format("Variable not found: variable=%s, lineno=%d, source=%s", variable,
											lineno, scriptFile.getAbsolutePath()));
						}
						line = left + value + right;
					} else {
						throw new SqlLoadException(String.format("Unmatched left braces: lineno=%d, source=%s", lineno,
								scriptFile.getAbsolutePath()));
					}
				}
				// Add line to the parameterize line list.
				parameterizedLines.add(line);
			}

			// Split into batches.
			batches = computeBatches(parameterizedLines);
		} catch (IOException e) {
			throw new SqlLoadException("Unable to read SQL script file: " + scriptFile, e);
		}
	}

	/**
	 * Returns SQL batches contained within the script. Contents are suppressed.
	 * 
	 * @throws SqlLoadException
	 */
	private List<SqlBatch> computeBatches(List<String> parameterizedLines) throws SqlLoadException {
		List<SqlBatch> batches = new ArrayList<SqlBatch>();
		StringBuffer batchContent = new StringBuffer();
		int lineno = 0;
		int startingLineno = -1;
		for (String line : parameterizedLines) {
			lineno++;
			line = line.trim();
			// Process cases.
			if (line.length() == 0) {
				// Empty line.
			} else if (line.startsWith(DIRECTIVE)) {
				// Directive. Find out which one.
				if (line.length() > DIRECTIVE.length()) {
					String directive = line.substring(DIRECTIVE.length()).trim();
					if (DIR_NON_TRANSACTIONAL.equalsIgnoreCase(directive)) {
						this.transactional = false;
					} else {
						throw new SqlLoadException(String.format("Unrecognized directive: line=%d, source=%s", lineno,
								this.scriptFile.getAbsolutePath()));
					}
				}
			} else if (line.startsWith(COMMENT)) {
				// Commment.
			} else if (line.startsWith(DELIMITER) && batchContent.length() == 0) {
				// Empty batch.
			} else if (line.startsWith(DELIMITER)) {
				// End of a batch. Append to list and start new batch.
				SqlBatch batch = new SqlBatch(this.scriptFile, startingLineno, batchContent.toString());
				batches.add(batch);
				batchContent = new StringBuffer();
				startingLineno = -1;
			} else {
				// Next line of current batch. Append content with space if there are multiple
				// lines.
				if (startingLineno < 0) {
					startingLineno = lineno;
				}
				if (batchContent.length() > 0) {
					batchContent.append(" ");
				}
				batchContent.append(line);
			}
		}

		// Look for an unterminated batch.
		if (batchContent.length() > 0) {
			throw new SqlLoadException(
					String.format("Unterminated batch: line=%d, source=%s", lineno, this.scriptFile.getAbsolutePath()));
		}

		return batches;
	}
}
