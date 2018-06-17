/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.TabularResultSet;

/**
 * Writes a query result set to CSV output on a writer.
 */
public class CsvBuilder {
	private TabularResultSet resultSet;
	private BufferedWriter writer;
	private boolean camelCaseNames;

	public CsvBuilder() {
	}

	public CsvBuilder resultSet(TabularResultSet resultSet) {
		this.resultSet = resultSet;
		return this;
	}

	public CsvBuilder writer(Writer w) {
		if (w instanceof BufferedWriter) {
			this.writer = (BufferedWriter) w;
		} else {
			this.writer = new BufferedWriter(w);
		}
		return this;
	}

	public CsvBuilder camelCaseNames(boolean camelCaseNames) {
		this.camelCaseNames = camelCaseNames;
		return this;
	}

	/** Create the extract output from the input result set. */
	public void write() throws IOException {
		// Create the CSV header by iterating across the row columns and emitting a
		// column header for each name.
		List<String> names = new ArrayList<String>();
		for (String columnName : resultSet.columnNames()) {
			if (camelCaseNames) {
				columnName = toCamelCase(columnName);
			}
			names.add(toCamelCase(columnName));
		}
		writeRow(names, false);

		// Create each succeeding row by iterating over the row columns and converting
		// the column value to a string.
		for (Row row : resultSet.rows()) {
			List<String> values = new ArrayList<String>();
			for (int i = 1; i <= resultSet.colCount(); i++) {
				String nextValue = row.getAsString(i);
				values.add(nextValue);
			}
			writeRow(values, true);
		}

		// Contents are buffered so we need to flush after writing everything.
		writer.flush();
	}

	/** Convert a snake_case name to camelCase. */
	private String toCamelCase(String snakeyName) {
		StringBuffer convertedName = new StringBuffer();
		boolean capitalize = false;
		for (char c : snakeyName.toCharArray()) {
			if (c == '_') {
				capitalize = true;
			} else if (capitalize) {
				convertedName.append(Character.toUpperCase(c));
				capitalize = false;
			} else {
				convertedName.append(Character.toLowerCase(c));
			}
		}
		return convertedName.toString();
	}

	/** Write a single CSV row. */
	private void writeRow(List<String> values, boolean newLine) throws IOException {
		// Each string is surrounded by double quotes. Embedded double quote
		// characters are escaped by adding an extra double quote. Each string
		// after the first must be preceded by a comma.
		if (newLine) {
			writer.newLine();
		}

		boolean firstValue = true;
		for (String value : values) {
			if (firstValue) {
				firstValue = false;
			} else {
				writer.write(",");
			}

			writer.write("\"");
			// Values may be null in which case we omit translation and generate an empty
			// string.
			if (value != null) {
				for (char c : value.toCharArray()) {
					if (c == '"') {
						writer.write(c);
					}
					writer.write(c);
				}
			}
			writer.write("\"");
		}
	}
}