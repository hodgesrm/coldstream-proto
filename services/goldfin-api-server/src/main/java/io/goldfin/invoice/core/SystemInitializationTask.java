/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.io.File;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.invoice.dbutils.ConnectionParams;
import io.goldfin.invoice.dbutils.SqlScriptExecutor;
import io.goldfin.invoice.utilities.FileHelper;

/**
 * Initializes full service.
 */
public class SystemInitializationTask implements Callable<TaskStatus> {
	static final Logger logger = LoggerFactory.getLogger(SystemInitializer.class);

	private final ProgressReporter progressReporter;
	private final SystemInitParams initParams;

	public SystemInitializationTask(SystemInitParams initParams, ProgressReporter reporter) {
		this.initParams = initParams;
		this.progressReporter = reporter;
	}

	/**
	 * Run SQL scripts to set up the service.
	 */
	@Override
	public TaskStatus call() {
		try {
			// Define the service user and database.
			ConnectionParams systemConnection = new ConnectionParams();
			systemConnection.setUrl(initParams.getUrl() + "/" + initParams.getAdminUser());
			systemConnection.setUser(initParams.getAdminUser());
			systemConnection.setPassword(initParams.getAdminPassword());

			Properties serviceProps = new Properties();
			serviceProps.setProperty("serviceUser", initParams.getServiceUser());
			serviceProps.setProperty("servicePassword", initParams.getServicePassword());
			serviceProps.setProperty("serviceDb", initParams.getServiceDb());

			File serviceInitScript = new File(FileHelper.homeDir(), "sql/init/service-init-01.sql");
			SqlScriptExecutor executor = new SqlScriptExecutor(systemConnection, serviceProps);
			executor.execute(serviceInitScript);
			progressReporter.progress("Installed service user and database", BigDecimal.valueOf(100.0));

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}