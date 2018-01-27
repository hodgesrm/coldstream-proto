/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Result set stored as a static table with named columns and numbered rows.
 */
public class TabularResultSet {
	private List<String> columnNames;
	private List<List<Object>> rowData = new ArrayList<List<Object>>();

	public TabularResultSet(ResultSet rs) throws SQLException {
		extract(rs);
	}

	private void extract(ResultSet rs) throws SQLException {
		// Set column names.
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();
		columnNames = new ArrayList<String>();
		for (int i = 1; i <= colCount; i++) {
			columnNames.add(meta.getColumnName(i));
		}

		// Set column data.
		while (rs.next()) {
			List<Object> nextRow = new ArrayList<Object>(colCount);
			for (int i = 1; i <= colCount; i++) {
				nextRow.add(rs.getObject(i));
			}
			rowData.add(nextRow);
		}
	}

	public int colCount() {
		return columnNames.size();
	}

	public int rowCount() {
		return rowData.size();
	}

	public List<String> columnNames() {
		return new ArrayList<String>(columnNames);
	}

	public Row row(int index) {
		return new Row(columnNames(), rowData.get(index - 1));
	}

	public List<Row> rows() {
		List<Row> rows = new ArrayList<Row>(rowCount());
		for (int i = 1; i <= rowCount(); i++) {
			rows.add(row(i));
		}
		return rows;
	}
}