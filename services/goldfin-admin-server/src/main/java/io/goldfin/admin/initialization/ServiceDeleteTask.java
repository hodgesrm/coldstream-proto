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
import io.goldfin.shared.data.DataException;
import io.goldfin.shared.data.DbHelper;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.tasks.AbstractTaskAdapter;
import io.goldfin.shared.tasks.ProgressReporter;
import io.goldfin.shared.tasks.TaskStatus;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Deletes a service.
 */
public class ServiceDeleteTask extends AbstractTaskAdapter {
	static final Logger logger = LoggerFactory.getLogger(ServiceDeleteTask.class);

	private final SystemInitParams initParams;
	private final boolean ignoreErrors;

	public ServiceDeleteTask(SystemInitParams initParams, boolean ignoreErrors, ProgressReporter progressReporter) {
		super(progressReporter);
		this.initParams = initParams;
		this.ignoreErrors = ignoreErrors;
	}

	public ServiceDeleteTask(SystemInitParams initParams, ProgressReporter progressReporter) {
		this(initParams, false, progressReporter);
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
			serviceProps.setProperty("serviceSchema", initParams.getServiceSchema());

			// Drop the service schema.
			try {
				ConnectionParams serviceConnection = DbHelper.tenantAdminConnectionParams(initParams);
				File adminInitScript = new File(FileHelper.homeDir(), "sql/admin-schema-remove-01.sql");
				SqlScriptExecutor adminExecutor = new SqlScriptExecutor(serviceConnection, serviceProps, null);
				adminExecutor.execute(adminInitScript);
				progressReporter.progress("Removed service schema", 50.0);
			} catch (DataException e) {
				if (ignoreErrors) {
					logger.info("Service schema removal failed: " + e.getMessage());
				} else {
					throw e;
				}
			}

			// Drop the service account.
			try {
				ConnectionParams systemConnection = DbHelper.systemConnectionParams(initParams);
				File serviceInitScript = new File(FileHelper.homeDir(), "sql/database-remove-01.sql");
				SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps, null);
				systemExecutor.execute(serviceInitScript);
				progressReporter.progress("Removed service database and user", 100.0);
			} catch (DataException e) {
				if (ignoreErrors) {
					logger.info("Database removal failed: " + e.getMessage());
				} else {
					throw e;
				}
			}

			return TaskStatus.successfulTask("Service removal succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("Service removal failed", this.getClass().getSimpleName(), e);
		}
	}
}