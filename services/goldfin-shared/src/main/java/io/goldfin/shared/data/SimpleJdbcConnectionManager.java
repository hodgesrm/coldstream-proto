/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opens connections to the DBMS.
 */
public class SimpleJdbcConnectionManager {
	static final Logger logger = LoggerFactory.getLogger(SimpleJdbcConnectionManager.class);

	private final ConcurrentHashMap<String, Driver> drivers = new ConcurrentHashMap<String, Driver>();
	private final ConnectionParams connectionParams;

	public SimpleJdbcConnectionManager(ConnectionParams connectionParams) {
		this.connectionParams = connectionParams;
	}

	/**
	 * Return an open DBMS connection
	 */
	public Connection open() {
		return open(null);
	}

	/**
	 * Return an open DBMS connection
	 * 
	 * @param schema
	 *            The default schema to use for queries or null if no schema default
	 *            is desired
	 */
	public Connection open(String schema) {
		// Add schema to URL if present.
		String url = null;
		if (schema == null) {
			url = connectionParams.getUrl();
		} else {
			url = connectionParams.getUrl() + "?currentSchema=" + schema;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Connecting to DBMS: driver=%s, url=%s, user=%s", connectionParams.getDriver(), url,
					connectionParams.getUser());
		}

		// Load the driver.
		Driver driver = drivers.get(connectionParams.getDriver());
		if (driver == null) {
			try {
				Class<?> driverClass = Class.forName(connectionParams.getDriver());
				driver = (Driver) driverClass.newInstance();
				drivers.put(connectionParams.getDriver(), driver);
			} catch (Exception e) {
				throw new RuntimeException("Unable to load JDBC driver: " + connectionParams.getDriver(), e);
			}
		}

		// Make a connection.
		Properties connectionProps = new Properties();
		connectionProps.put("user", connectionParams.getUser());
		connectionProps.put("password", connectionParams.getPassword());
		try {
			Connection connection = driver.connect(url, connectionProps);
			return connection;
		} catch (SQLException e) {
			throw new RuntimeException("Unable to connect to DBMS", e);
		}
	}

	/**
	 * Close a DBMS connection.
	 */
	public void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.warn("Unable to close DBMS connection");
			}
		}
	}
}