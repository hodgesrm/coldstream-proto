/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Performs a SQL DELETE using a prepared statement.
 */
public class SqlDelete {
	private String table;
	private String where;
	private Object[] whereParams;

	public SqlDelete() {
	}

	public SqlDelete table(String table) {
		this.table = table;
		return this;
	}

	public SqlDelete where(String where, Object... value) {
		this.where = where;
		this.whereParams = value;
		return this;
	}

	public int run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			// Generate insert SQL.
			String format = "DELETE FROM %s WHERE %s";
			String query = String.format(format, table, where);

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(query);
			for (int i = 0; i < whereParams.length; i++) {
				pstmt.setObject(i + 1, whereParams[i]);
			}

			// Execute and return the result.
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataException(e.getLocalizedMessage(), e);
		} finally {
			JdbcUtils.closeSoftly(resultSet);
			JdbcUtils.closeSoftly(pstmt);
		}
	}
}