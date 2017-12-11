/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs a SQL INSERT using a prepared statement.
 */
public class SqlInsert {
	static final Logger logger = LoggerFactory.getLogger(SqlInsert.class);

	private String table;
	private Map<String, Object> values = new HashMap<String, Object>();
	private List<String> names = new LinkedList<String>();

	public SqlInsert() {
	}

	public SqlInsert table(String table) {
		this.table = table;
		return this;
	}

	public SqlInsert put(String name, Object value) {
		if (values.get(name) == null) {
			names.add(name);
		}
		values.put(name, value);
		return this;
	}

	public int run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		try {
			// Generate insert SQL.
			String format = "INSERT INTO %s (%s) values (%s)";
			StringBuffer columnList = new StringBuffer();
			StringBuffer paramList = new StringBuffer();
			for (int i = 0; i < names.size(); i++) {
				if (i > 0) {
					columnList.append(", ");
					paramList.append(", ");
				}
				columnList.append(names.get(i));
				paramList.append("?");
			}
			String sql = String.format(format, table, columnList, paramList);
			if (logger.isDebugEnabled()) {
				logger.debug("Insert statement: " + sql);
			}

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(sql);
			int index = 1;
			for (String name : names) {
				pstmt.setObject(index++, values.get(name));
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