/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a transactional session with participating services.
 */
public class SessionBuilder {
	private enum SessionType {
		ADMIN, TENANT, NO_SCHEMA
	}

	private SimpleJdbcConnectionManager connectionManager;
	private List<TransactionalService> services = new ArrayList<TransactionalService>();
	private SessionType type;
	private String tenantName;

	public SessionBuilder() {
	}

	public SessionBuilder connectionManager(SimpleJdbcConnectionManager manager) {
		this.connectionManager = manager;
		return this;
	}

	public SessionBuilder noSchemaType() {
		this.type = SessionType.NO_SCHEMA;
		return this;
	}

	public SessionBuilder adminType() {
		this.type = SessionType.ADMIN;
		return this;
	}

	public SessionBuilder tenantType(String tenantName) {
		this.type = SessionType.TENANT;
		this.tenantName = tenantName;
		return this;
	}

	public SessionBuilder addService(TransactionalService service) {
		this.services.add(service);
		return this;
	}

	/**
	 * Returns a ready-to-use session with services enlisted.
	 */
	public Session build() {
		// Set schema correctly for admin or tenant operations.
		String schema;
		if (SessionType.ADMIN.equals(type)) {
			schema = "admin";
		} else if (SessionType.TENANT.equals(type)) {
			schema = "tenant_" + tenantName;
		} else if (SessionType.NO_SCHEMA.equals(type)) {
			schema = null;
		} else {
			throw new RuntimeException("Unknown transaction type");
		}

		// Open the connection.
		Connection conn = connectionManager.open(schema);

		// Create session and enlist services.
		Session session = new Session(conn, schema);
		for (TransactionalService service : services) {
			service.setSession(session);
		}

		// Start transaction and return.
		session.begin();
		return session;
	}
}