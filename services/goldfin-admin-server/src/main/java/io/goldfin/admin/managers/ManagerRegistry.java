/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;

/**
 * Singleton class that provides access to managers.
 */
public class ManagerRegistry implements ManagementContext {
	static private final Logger logger = LoggerFactory.getLogger(ManagerRegistry.class);

	// Singleton registry.
	private static final ManagerRegistry registry = new ManagerRegistry();

	// Registry state.
	private Map<String, Manager> managers = new HashMap<String, Manager>();
	private ConnectionParams connectionParams;
	private SimpleJdbcConnectionManager connectionManager;

	// Standard way to get a properly initialized manager
	public static ManagerRegistry getInstance() {
		return registry;
	}

	public void initialize(ConnectionParams connectionParams) {
		this.connectionParams = connectionParams;
		this.connectionManager = new SimpleJdbcConnectionManager(this.connectionParams);
	}

	public void addManager(String name, Manager manager) {
		managers.put(name, manager);
	}

	public Manager getManager(String name) {
		return managers.get(name);
	}

	public void start() {
		// Assign management context to each manager.
		for (String name : managers.keySet()) {
			Manager manager = managers.get(name);
			logger.info(
					String.format("Assigning context: manager=%s, class=%s", name, manager.getClass().getSimpleName()));
			manager.setContext(this);
		}

		// Prepare managers for operation.
		for (String name : managers.keySet()) {
			Manager manager = managers.get(name);
			logger.info(String.format("Preparing for operation: manager=%s, class=%s", name,
					manager.getClass().getSimpleName()));
			manager.prepare();
		}
	}

	public void shutdown() {
		// Prepare managers for operation.
		for (String name : managers.keySet()) {
			Manager manager = managers.get(name);
			logger.info(String.format("Shutting down: manager=%s, class=%s", name, manager.getClass().getSimpleName()));
			manager.release();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getConnectionManager()
	 */
	@Override
	public SimpleJdbcConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getAdminSchema()
	 */
	@Override
	public String getAdminSchema() {
		return this.connectionParams.getAdminSchema();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.goldfin.admin.managers.ManagementContext#adminSession(io.goldfin.shared.
	 * data.TransactionalService[])
	 */
	@Override
	public Session adminSession(TransactionalService<?>... svcs) {
		SimpleJdbcConnectionManager cm = getConnectionManager();
		String schema = getAdminSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).addServices(svcs).build();
	}
}