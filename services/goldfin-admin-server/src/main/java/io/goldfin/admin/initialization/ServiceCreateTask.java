/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.TenantManager;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.shared.config.ServiceConfig;
import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.data.DbHelper;
import io.goldfin.shared.data.DbmsParams;
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
	private final ServiceConfig serviceParams;

	private final String ADMIN_SCHEMA = "admin";

	public ServiceCreateTask(SystemInitParams initParams, ServiceConfig serviceParams, ProgressReporter reporter) {
		super(reporter);
		this.initParams = initParams;
		this.serviceParams = serviceParams;
	}

	/**
	 * Run SQL scripts to set up the service.
	 */
	@Override
	public TaskStatus call() {
		try {
			// Set properties of DBMS that will be created for service.
			Properties serviceProps = new Properties();
			DbmsParams dbmsParams = serviceParams.getDbms();
			serviceProps.setProperty("serviceUser", dbmsParams.getUser());
			serviceProps.setProperty("servicePassword", dbmsParams.getPassword());
			serviceProps.setProperty("serviceDb", dbmsParams.getUser());
			serviceProps.setProperty("serviceSchema", dbmsParams.getAdminSchema());

			// Initialize the service user and database.
			DbmsParams systemConnection = DbHelper.systemConnectionParams(initParams);
			File serviceInitScript = new File(FileHelper.homeDir(), "sql/database-init-01.sql");
			SqlScriptExecutor systemExecutor = new SqlScriptExecutor(systemConnection, serviceProps, null);
			systemExecutor.execute(serviceInitScript);
			progressReporter.progress(String.format("Installed service database: user=%s, database=%s",
					dbmsParams.getUser(), dbmsParams.getUser()), 33.0);

			// Initialize the service schema.
			File adminInitScript = new File(FileHelper.homeDir(), "sql/admin-schema-init-01.sql");
			SqlScriptExecutor adminExecutor = new SqlScriptExecutor(dbmsParams, serviceProps, ADMIN_SCHEMA);
			adminExecutor.execute(adminInitScript);
			progressReporter.progress(String.format("Set up master service tables: schema=%s", ADMIN_SCHEMA), 66.0);

			// Create the system tenant and the sysadmin user thereof.
			ManagerRegistry registry = new ManagerRegistry();
			TenantManager tenantManager = new TenantManager();
			UserManager userManager = new UserManager();
			// Need to extend initialization to cover AWS resource setup.
			registry.initialize(serviceParams);
			registry.addManager(tenantManager);
			registry.addManager(userManager);
			registry.start();

			// Create the system tenant.
			tenantManager.createSystemTenant();

			UserParameters userParams = new UserParameters();
			userParams.setUser(initParams.getSysUser() + "@system");
			userParams.setInitialPassword(initParams.getSysPassword());
			userParams.setRoles("admin");
			userManager.createUser(userParams);
			progressReporter.progress(String.format("Set up sysadmin user: username=%s", userParams.getUser()), 100.0);

			return TaskStatus.successfulTask("System initialization succeeded", this.getClass().getSimpleName());
		} catch (Exception e) {
			return TaskStatus.failedTask("System initialization failed", this.getClass().getSimpleName(), e);
		}
	}
}