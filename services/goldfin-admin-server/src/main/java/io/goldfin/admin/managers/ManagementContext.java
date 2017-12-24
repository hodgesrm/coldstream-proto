/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;

/**
 * Denotes a context that supplies configuration information to managers.
 */
public interface ManagementContext {
	/** Returns the DBMS connection parameters. */
	public ConnectionParams getConnectionParams();

	/** Returns the DBMS connection manager. */
	public SimpleJdbcConnectionManager getConnectionManager();

	/** Returns the admin schema name. */
	public String getAdminSchema();

	/**
	 * Returns an administrative session with zero or more associated transactional
	 * services.
	 */
	public Session adminSession(TransactionalService<?>... svcs);
}