/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages a transactional DBMS session on a single DBMS connection on a single
 * schema.
 */
public class Session implements Closeable {
	private final Connection connection;
	private final String schema;

	Session(Connection connection, String schema) {
		this.connection = connection;
		this.schema = schema;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getSchema() {
		return schema;
	}

	// Transaction management.
	public void begin() throws DataException {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DataException("Unable to start transaction", e);
		}
	}

	public void commit() throws DataException {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DataException("Unable to commit transaction", e);
		}
	}

	public void rollback() throws DataException {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new DataException("Unable to roll back transaction", e);
		}
	}

	// Resource management.
	@Override
	public void close() throws IOException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// Ignore
			}
		}
	}
}