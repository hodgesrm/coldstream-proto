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
	private Map<Class<?>, Manager> managers = new HashMap<Class<?>, Manager>();
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

	public void addManager(Manager manager) {
		managers.put(manager.getClass(), manager);
	}

	@SuppressWarnings("unchecked")
	public <T> T getManager(Class<T> managerClass) {
		return (T) managers.get(managerClass);
	}

	public void start() {
		// Assign management context to each manager.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Assigning context: manager=%s", managerClass.getSimpleName()));
			manager.setContext(this);
		}

		// Prepare managers for operation.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Preparing for operation: manager=%s", managerClass.getSimpleName()));
			manager.prepare();
		}
	}

	public void shutdown() {
		// Prepare managers for operation.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Preparing for operation: manager=%s", managerClass.getSimpleName()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getConnectionParams()
	 */
	@Override
	public ConnectionParams getConnectionParams() {
		return this.connectionParams;
	}
}