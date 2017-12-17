/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.DbHelper;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.tasks.AbstractTaskAdapter;
import io.goldfin.shared.tasks.ProgressReporter;
import io.goldfin.shared.tasks.TaskStatus;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Initializes full service.
 */
public class ServiceCreateTask extends AbstractTaskAdapter {
	static final Logger logger = LoggerFactory.getLogger(ServiceCreateTask.class);

	private final SystemInitParams initParams;
	
	private final String ADMIN_SCHEMA = "admin";

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
			serviceProps.setProperty("adminSchema", ADMIN_SCHEMA);

			// Initialize the service user and database.
			ConnectionParams systemConnection = DbHelper.systemConnectionParams(initParams);
			File serviceInitScript = new File(FileHelper.homeDir(), "sql/service-init-01.sql");
			SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps, null);
			systemExecutor.execute(serviceInitScript);
			progressReporter.progress("Installed service user and database", 50.0);

			// Initialize the service schema.
			ConnectionParams serviceConnection = DbHelper.tenantAdminConnectionParams(initParams);
			File adminInitScript = new File(FileHelper.homeDir(), "sql/admin-schema-init-01.sql");
			SqlScriptExecutor adminExecutor = new SqlScriptExecutor(serviceConnection, serviceProps, ADMIN_SCHEMA);
			adminExecutor.execute(adminInitScript);
			progressReporter.progress("Set up service schema", 100.0);

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}