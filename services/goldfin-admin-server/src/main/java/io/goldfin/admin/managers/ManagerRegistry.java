/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.SimpleJdbcConnectionManager;

/**
 * Singleton class that provides access to managers.
 */
public class ManagerRegistry implements ManagementContext {
	static private final Logger logger = LoggerFactory.getLogger(ManagerRegistry.class);
	private Map<String, Manager> managers = new HashMap<String, Manager>();

	private static final ManagerRegistry registry = new ManagerRegistry();

	public static ManagerRegistry getInstance() {
		return registry;
	}

	public void addManager(String name, Manager manager) {
		managers.put(name, manager);
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

	/* (non-Javadoc)
	 * @see io.goldfin.admin.managers.ManagementContext#getConnectionManager()
	 */
	@Override
	public SimpleJdbcConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see io.goldfin.admin.managers.ManagementContext#getAdminSchema()
	 */
	@Override
	public String getAdminSchema() {
		// TODO Auto-generated method stub
		return null;
	}
}