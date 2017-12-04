/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.dbutils;

import io.goldfin.shared.config.SystemInitParams;

/**
 * Various helper methods for working with databases. 
 */
public class DbHelper {
	/**
	 * Get connection parameters for DBMS server. 
	 */
	public static ConnectionParams systemConnectionParams(SystemInitParams initParams) {
		ConnectionParams systemConnection = new ConnectionParams();
		systemConnection.setUrl(initParams.getUrl() + "/" + initParams.getAdminUser());
		systemConnection.setUser(initParams.getAdminUser());
		systemConnection.setPassword(initParams.getAdminPassword());
		return systemConnection;
	}

	/**
	 * Get connection parameters for master tenant admin service. 
	 */
	public static ConnectionParams tenantAdminConnectionParams(SystemInitParams initParams) {
		ConnectionParams serviceConnection = new ConnectionParams();
		serviceConnection.setUrl(initParams.getUrl() + "/" + initParams.getAdminUser());
		serviceConnection.setUser(initParams.getAdminUser());
		serviceConnection.setPassword(initParams.getAdminPassword());
		return serviceConnection;
	}
}