/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.invoice.dbutils.ConnectionParams;
import io.goldfin.invoice.dbutils.DbHelper;
import io.goldfin.invoice.dbutils.SqlScriptExecutor;
import io.goldfin.invoice.utilities.FileHelper;

/**
 * Initializes full service.
 */
public class ServiceCreateTask extends AbstractTaskAdapter {
	static final Logger logger = LoggerFactory.getLogger(ServiceCreateTask.class);

	private final SystemInitParams initParams;

	public ServiceCreateTask(SystemInitParams initParams, ProgressReporter reporter) {
		super(reporter);
		this.initParams = initParams;
	}

	/**
	 * Run SQL scripts to set up the service.
	 */
	@Override
	public TaskStatus call() {
		try {
			// Define a system connection for initialization and a service connection.
			Properties serviceProps = new Properties();
			serviceProps.setProperty("serviceUser", initParams.getServiceUser());
			serviceProps.setProperty("servicePassword", initParams.getServicePassword());
			serviceProps.setProperty("serviceDb", initParams.getServiceDb());

			// Initialize the service user and database.
			ConnectionParams systemConnection = DbHelper.systemConnectionParams(initParams);
			File serviceInitScript = new File(FileHelper.homeDir(), "sql/init/service-init-01.sql");
			SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps);
			systemExecutor.execute(serviceInitScript);
			progressReporter.progress("Installed service user and database", 50.0);

			// Initialize the service schema.
			ConnectionParams serviceConnection = DbHelper.tenantAdminConnectionParams(initParams);
			File adminInitScript = new File(FileHelper.homeDir(), "sql/init/admin-init-01.sql");
			SqlScriptExecutor adminExecutor = new SqlScriptExecutor(serviceConnection, serviceProps);
			adminExecutor.execute(adminInitScript);
			progressReporter.progress("Set up service schema", 100.0);

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}