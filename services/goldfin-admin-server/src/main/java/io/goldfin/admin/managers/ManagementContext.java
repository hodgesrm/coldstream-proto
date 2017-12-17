/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import io.goldfin.shared.data.SimpleJdbcConnectionManager;

/**
 * Denotes a context that supplies configuration information to managers.
 */
public interface ManagementContext {
	public SimpleJdbcConnectionManager getConnectionManager();

	public String getAdminSchema();
}