/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Provides helper data for DBMS connection testing. This class *does not* use
 * the configuration search path for YAML config files since it's for unit
 * tests.
 */
public class DbConnectionHelper {
	static final Logger logger = LoggerFactory.getLogger(DbConnectionHelper.class);

	protected final ConnectionParams testDbParams;
	protected final SimpleJdbcConnectionManager connectionManager;
	protected final String schema;

	/** Load DBMS connection parameters from yaml. */
	public DbConnectionHelper(String schema, String dbParamFile) throws Exception {
		this.schema = schema;
		this.testDbParams = YamlHelper.readFromClasspath(dbParamFile, ConnectionParams.class);
		this.connectionManager = new SimpleJdbcConnectionManager(testDbParams);
	}

	public DbConnectionHelper(String schema) throws Exception {
		this(schema, "dbms.yaml");
	}

	public String getSchema() {
		return schema;
	}

	public ConnectionParams getTestDbParams() {
		return testDbParams;
	}

	public SimpleJdbcConnectionManager getConnectionManager() {
		return connectionManager;
	}
}