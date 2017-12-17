/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a transactional session with participating services.
 */
public class SessionBuilder {
	private SimpleJdbcConnectionManager connectionManager;
	private List<TransactionalService<?>> services = new ArrayList<TransactionalService<?>>();
	private String schema;
	private boolean ensure = false;

	public SessionBuilder() {
	}

	public SessionBuilder connectionManager(SimpleJdbcConnectionManager manager) {
		this.connectionManager = manager;
		return this;
	}

	/** Use an existing schema as context for all queries. */
	public SessionBuilder useSchema(String schema) {
		this.schema = schema;
		return this;
	}

	/** Ensure the schema exists and use it as context for all queries. */
	public SessionBuilder ensureSchema(String schema) {
		this.ensure = true;
		return useSchema(schema);
	}

	public SessionBuilder addService(TransactionalService<?> service) {
		this.services.add(service);
		return this;
	}

	/**
	 * Returns a ready-to-use session with services enlisted.
	 */
	public Session build() {
		// If we need to ensure schema exists, do that now.
		if (ensure) {
			createSchemaIfRequired();
		}

		// Open the connection.
		Connection conn = connectionManager.open(schema);

		// Create session and enlist services.
		Session session = new Session(conn, schema);
		for (TransactionalService<?> service : services) {
			service.setSession(session);
		}

		// Start transaction and return.
		session.begin();
		return session;
	}

	private void createSchemaIfRequired() {
		Connection conn = connectionManager.open();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
		} catch (SQLException e) {
			throw new DataException("Unable to ensure schema exists: " + schema, e);
		} finally {
			JdbcUtils.closeSoftly(conn);
		}
	}
}