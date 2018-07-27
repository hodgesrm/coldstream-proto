/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.testing;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.config.ServiceConfig;
import io.goldfin.shared.data.DbmsParams;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.utilities.FileHelper;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Provides helper data for DBMS connection testing. 
 */
public class DbConnectionHelper {
	static final Logger logger = LoggerFactory.getLogger(DbConnectionHelper.class);

	protected final DbmsParams testDbParams;
	protected final SimpleJdbcConnectionManager connectionManager;
	protected final String schema;

	/** Load DBMS connection parameters from service.yaml. */
	public DbConnectionHelper(String schema) throws Exception {
		this.schema = schema;
		File baseServiceConfigFile = FileHelper.getConfigFile("service.yaml");
		ServiceConfig serviceConfig = YamlHelper.readFromFile(baseServiceConfigFile, ServiceConfig.class);
		this.testDbParams = serviceConfig.getDbms();
		this.connectionManager = new SimpleJdbcConnectionManager(testDbParams);
	}

	public String getSchema() {
		return schema;
	}

	public DbmsParams getTestDbParams() {
		return testDbParams;
	}

	public SimpleJdbcConnectionManager getConnectionManager() {
		return connectionManager;
	}
}