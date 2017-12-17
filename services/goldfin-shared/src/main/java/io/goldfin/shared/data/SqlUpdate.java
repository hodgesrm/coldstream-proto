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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs a SQL UPDATE using a prepared statement.
 */
public class SqlUpdate {
	static final Logger logger = LoggerFactory.getLogger(SqlUpdate.class);

	private String table;
	private Map<String, Object> values = new HashMap<String, Object>();
	private List<String> names = new LinkedList<String>();
	private String where;
	private Object[] whereParams;

	public SqlUpdate() {
	}

	public SqlUpdate table(String table) {
		this.table = table;
		return this;
	}

	public SqlUpdate put(String name, Object value) {
		if (values.get(name) == null) {
			names.add(name);
		}
		values.put(name, value);
		return this;
	}

	public SqlUpdate id(String id) {
		return where("id = ?", id);
	}

	public SqlUpdate id(UUID id) {
		return where("id = ?", id);
	}

	public SqlUpdate where(String where, Object... value) {
		this.where = where;
		this.whereParams = value;
		return this;
	}

	public int run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		try {
			// Generate insert SQL.
			String format = "UPDATE %s SET %s WHERE %s";
			StringBuffer updatePairs = new StringBuffer();
			for (int i = 0; i < names.size(); i++) {
				if (i > 0) {
					updatePairs.append(", ");
				}
				updatePairs.append(names.get(i)).append(" = ?");
			}
			String sql = String.format(format, table, updatePairs, where);
			if (logger.isDebugEnabled()) {
				logger.debug("Update statement: " + sql);
			}

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(sql);
			int index = 1;
			for (String name : names) {
				pstmt.setObject(index++, values.get(name));
			}
			for (int i = 0; i < whereParams.length; i++) {
				pstmt.setObject(index++, whereParams[i]);
			}

			// Execute and return the result.
			return pstmt.executeUpdate();
		} catch (

		SQLException e) {
			throw new DataException(e.getLocalizedMessage(), e);
		} finally {
			JdbcUtils.closeSoftly(pstmt);
		}
	}
}