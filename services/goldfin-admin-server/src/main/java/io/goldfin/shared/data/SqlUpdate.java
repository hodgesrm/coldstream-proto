/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs a SQL UPDATE using a prepared statement.
 */
public class SqlUpdate {
	static final Logger logger = LoggerFactory.getLogger(SqlUpdate.class);

	private String table;
	private List<InsertValue> values = new LinkedList<InsertValue>();
	private String where;
	private Object[] whereParams;

	public SqlUpdate() {
	}

	public int updatedColumnsSize() {
		return values.size();
	}

	public SqlUpdate table(String table) {
		this.table = table;
		return this;
	}

	public SqlUpdate put(String name, Object value) {
		return put(name, value, false);
	}

	public SqlUpdate put(String name, Object value, boolean json) {
		// Fixup--convert java.util.Date to timestamp so PostgreSQL can work with it.
		if (value != null && value instanceof java.util.Date) {
			java.util.Date dateValue = (java.util.Date) value;
			value = new Timestamp(dateValue.getTime());
		}
		InsertValue iv = new InsertValue();
		iv.name = name;
		iv.value = value;
		iv.json = json;
		values.add(iv);
		return this;
	}

	public SqlUpdate id(long id) {
		return where("id = ?", id);
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
			for (int i = 0; i < values.size(); i++) {
				InsertValue value = values.get(i);
				if (i > 0) {
					updatePairs.append(", ");
				}
				if (value.json) {
					updatePairs.append(value.name).append(" = ?::JSON");
				} else {
					updatePairs.append(value.name).append(" = ?");
				}
			}
			String sql = String.format(format, table, updatePairs, where);
			if (logger.isDebugEnabled()) {
				logger.debug("Update statement: " + sql);
			}

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(sql);
			int index = 1;
			for (InsertValue iv : values) {
				pstmt.setObject(index++, iv.value);
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