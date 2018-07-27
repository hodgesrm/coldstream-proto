/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import io.goldfin.shared.cloud.AwsParams;
import io.goldfin.shared.config.DataSeriesParams;
import io.goldfin.shared.config.GatewayParams;
import io.goldfin.shared.config.OcrParams;
import io.goldfin.shared.data.DbmsParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;

/**
 * Denotes a context that supplies configuration information to managers.
 */
public interface ManagementContext {
	/** Returns AWS connection parameters. */
	public AwsParams getAwsConnectionParams();

	/** Return gateway configuration parameters. */
	public GatewayParams getGatewayParams();

    /** Return OCR configuration parameters. */
	public OcrParams getOcrParams();
	
	/** Return data series processing parameters. */
	public DataSeriesParams getDataSeriesParams();
	
	/** Returns the DBMS connection parameters. */
	public DbmsParams getConnectionParams();

	/** Returns the DBMS connection manager. */
	public SimpleJdbcConnectionManager getConnectionManager();

	/** Returns the admin schema name. */
	public String getAdminSchema();

	/**
	 * Returns an administrative session with zero or more associated transactional
	 * services.
	 */
	public Session tenantSession(String tenantId);

	/**
	 * Returns an administrative session with zero or more associated transactional
	 * services.
	 */
	public Session adminSession();
}