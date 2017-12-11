/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Executes a SQL statement.
 */
public class SqlStatement {
	public final String query;

	public SqlStatement(String query) {
		this.query = query;
	}

	public int run(Session session) throws DataException {
		Statement stmt = null;
		try {
			stmt = session.getConnection().createStatement();
			return stmt.executeUpdate(query);
		} catch (SQLException e) {
			throw new DataException(e.getLocalizedMessage(), e);
		} finally {
			JdbcUtils.closeSoftly(stmt);
		}
	}
}