/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.dbutils.SqlLoadException;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Utility class to help with implementation of tests.
 */
public class AdminTestHelper {
	/** Load admin tables into a designated schema. */
	public static void loadAdminSchema(ConnectionParams connectionParams, String schema) throws SqlLoadException {
		// Remove old schema.
		Properties serviceProps = new Properties();
		serviceProps.setProperty("serviceUser", connectionParams.getUser());
		serviceProps.setProperty("serviceSchema", schema);
		SqlScriptExecutor adminExecutor = new SqlScriptExecutor(connectionParams, serviceProps, schema);

		File adminRemoveScript = new File(FileHelper.homeDir(), "sql/admin-schema-remove-01.sql");
		adminExecutor.execute(adminRemoveScript);

		File adminInitScript = new File(FileHelper.homeDir(), "sql/admin-schema-init-01.sql");
		adminExecutor.execute(adminInitScript);
	}

	/** Create a tenant for testing purpose. */
	public static String createTenant(ConnectionParams connectionParams, String schema, String name)
			throws SqlLoadException {
		SimpleJdbcConnectionManager cm = new SimpleJdbcConnectionManager(connectionParams);
		TransactionalService<Tenant> svc = new TenantDataService();

		try (Session session = new SessionBuilder().connectionManager(cm).useSchema(schema).addService(svc).build();) {
			Tenant t = new Tenant();
			t.setName(name);
			t.setDescription("Test tenant");
			String id = svc.create(t);
			session.commit();
			return id;
		}
	}

	/** Create a user for testing purpose. */
	public static String createUser(ConnectionParams connectionParams, String schema, String tenantId, String username)
			throws SqlLoadException {
		SimpleJdbcConnectionManager cm = new SimpleJdbcConnectionManager(connectionParams);
		TransactionalService<UserData> svc = new UserDataService();

		try (Session session = new SessionBuilder().connectionManager(cm).useSchema(schema).addService(svc).build();) {
			UserData u = new UserData();
			u.setUsername(username);
			u.setTenantId(UUID.fromString(tenantId));
			u.setRoles("");
			String id = svc.create(u);
			session.commit();
			return id;
		}
	}
	// testUserId = AdminTestHelper.createBaseTenant(dch.getTestDbParams(),
	// dch.getSchema(), testTenantId, "test_user");

}