/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs a SQL INSERT using a prepared statement.
 */
public class SqlInsert {
	static final Logger logger = LoggerFactory.getLogger(SqlInsert.class);

	class InsertValue {
		String name;
		Object value;
		boolean json;
	};

	private String table;
	private List<InsertValue> values = new LinkedList<InsertValue>();

	public SqlInsert() {
	}

	public SqlInsert table(String table) {
		this.table = table;
		return this;
	}

	public SqlInsert put(String name, Object value) {
		return put(name, value, false);
	}

	public SqlInsert put(String name, Object value, boolean json) {
		InsertValue iv = new InsertValue();
		iv.name = name;
		iv.value = value;
		iv.json = json;
		values.add(iv);
		return this;
	}

	public int run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		try {
			// Generate insert SQL.
			String format = "INSERT INTO %s (%s) values (%s)";
			StringBuffer columnList = new StringBuffer();
			StringBuffer paramList = new StringBuffer();
			for (int i = 0; i < values.size(); i++) {
				InsertValue iv = values.get(i);
				if (i > 0) {
					columnList.append(", ");
					paramList.append(", ");
				}
				columnList.append(iv.name);
				if (iv.json) {
					paramList.append("?::JSON");
				} else {
					paramList.append("?");
				}
			}
			String sql = String.format(format, table, columnList, paramList);
			if (logger.isDebugEnabled()) {
				logger.debug("Insert statement: " + sql);
			}

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(sql);
			int index = 1;
			for (InsertValue iv : values) {
				pstmt.setObject(index++, iv.value);
			}

			// Execute and return the result.
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataException(e.getLocalizedMessage(), e);
		} finally {
			JdbcUtils.closeSoftly(pstmt);
		}
	}
}