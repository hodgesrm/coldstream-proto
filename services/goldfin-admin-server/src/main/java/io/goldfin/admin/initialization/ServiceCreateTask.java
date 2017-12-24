/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.DbHelper;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.tasks.AbstractTaskAdapter;
import io.goldfin.shared.tasks.ProgressReporter;
import io.goldfin.shared.tasks.TaskStatus;
import io.goldfin.shared.utilities.FileHelper;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Initializes full service.
 */
public class ServiceCreateTask extends AbstractTaskAdapter {
	static final Logger logger = LoggerFactory.getLogger(ServiceCreateTask.class);

	private final SystemInitParams initParams;
	private final File connectionParamsFile;

	private final String ADMIN_SCHEMA = "admin";

	public ServiceCreateTask(SystemInitParams initParams, File connectionParamsFile, ProgressReporter reporter) {
		super(reporter);
		this.initParams = initParams;
		this.connectionParamsFile = connectionParamsFile;
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
			serviceProps.setProperty("serviceSchema", ADMIN_SCHEMA);

			// Initialize the service user and database.
			ConnectionParams systemConnection = DbHelper.systemConnectionParams(initParams);
			File serviceInitScript = new File(FileHelper.homeDir(), "sql/database-init-01.sql");
			SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps, null);
			systemExecutor.execute(serviceInitScript);
			progressReporter.progress(String.format("Installed service database: user=%s, database=%s",
					initParams.getServiceUser(), initParams.getServiceDb()), 33.0);

			// Initialize the service schema.
			ConnectionParams serviceConnection = DbHelper.tenantAdminConnectionParams(initParams);
			File adminInitScript = new File(FileHelper.homeDir(), "sql/admin-schema-init-01.sql");
			SqlScriptExecutor adminExecutor = new SqlScriptExecutor(serviceConnection, serviceProps, ADMIN_SCHEMA);
			adminExecutor.execute(adminInitScript);
			progressReporter.progress(String.format("Set up master service tables: schema=%s", ADMIN_SCHEMA), 66.0);

			// Write connection parameters.
			YamlHelper.writeToFile(this.connectionParamsFile, serviceConnection);
			progressReporter.progress(
					String.format("Write connection parameters file: %s", this.connectionParamsFile.getAbsolutePath()),
					75.0);

			// Create the sysadmin user.
			ManagerRegistry registry = new ManagerRegistry();
			UserManager userManager = new UserManager();
			registry.initialize(serviceConnection);
			registry.addManager(userManager);
			registry.start();
			UserParameters userParams = new UserParameters();
			userParams.setUsername(initParams.getSysUser());
			userParams.setInitialPassword(initParams.getSysPassword());
			userParams.setRoles("admin");
			userManager.createUser(userParams);
			progressReporter.progress(String.format("Set up sysadmin user: username=%s", userParams.getUsername()),
					100.0);

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}