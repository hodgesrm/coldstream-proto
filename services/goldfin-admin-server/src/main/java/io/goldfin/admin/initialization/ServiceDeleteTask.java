/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.dbutils.ConnectionParams;
import io.goldfin.shared.dbutils.DbHelper;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.tasks.AbstractTaskAdapter;
import io.goldfin.shared.tasks.ProgressReporter;
import io.goldfin.shared.tasks.TaskStatus;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Initializes full service.
 */
public class ServiceDeleteTask extends AbstractTaskAdapter {
	static final Logger logger = LoggerFactory.getLogger(ServiceDeleteTask.class);

	private final SystemInitParams initParams;

	public ServiceDeleteTask(SystemInitParams initParams, ProgressReporter progressReporter) {
		super(progressReporter);
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

			// Drop the service schema.
			ConnectionParams serviceConnection = DbHelper.tenantAdminConnectionParams(initParams);
			File adminInitScript = new File(FileHelper.homeDir(), "sql/init/admin-remove-01.sql");
			SqlScriptExecutor adminExecutor = new SqlScriptExecutor(serviceConnection, serviceProps);
			adminExecutor.execute(adminInitScript);
			progressReporter.progress("Removed service schema", 50.0);

			// Drop the service account.
			ConnectionParams systemConnection = DbHelper.systemConnectionParams(initParams);
			File serviceInitScript = new File(FileHelper.homeDir(), "sql/init/service-remove-01.sql");
			SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps);
			systemExecutor.execute(serviceInitScript);
			progressReporter.progress("Removed service database and user", 100.0);

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}