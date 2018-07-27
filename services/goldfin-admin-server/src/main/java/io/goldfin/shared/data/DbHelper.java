/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import io.goldfin.shared.config.SystemInitParams;

/**
 * Various helper methods for working with databases.
 */
public class DbHelper {
	/**
	 * Get connection parameters for DBMS server.
	 */
	public static DbmsParams systemConnectionParams(SystemInitParams initParams) {
		DbmsParams systemConnection = new DbmsParams();
		systemConnection.setUrl(initParams.getUrl());
		systemConnection.setUser(initParams.getAdminUser());
		systemConnection.setPassword(initParams.getAdminPassword());
		return systemConnection;
	}
}