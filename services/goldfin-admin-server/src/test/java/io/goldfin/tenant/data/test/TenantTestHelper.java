/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.io.File;
import java.util.Properties;

import io.goldfin.shared.data.DbmsParams;
import io.goldfin.shared.dbutils.SqlLoadException;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Utility class to help with implementation of tests.
 */
public class TenantTestHelper {
	/** Load admin tables into a designated schema. */
	public static void loadTenantSchema(DbmsParams connectionParams, String schema) throws SqlLoadException {
		// Remove old schema.
		Properties tenantProps = new Properties();
		tenantProps.setProperty("tenantSchema", schema);
		SqlScriptExecutor adminExecutor = new SqlScriptExecutor(connectionParams, tenantProps, schema);

		File adminRemoveScript = new File(FileHelper.homeDir(), "sql/tenant-schema-remove-01.sql");
		adminExecutor.execute(adminRemoveScript);

		File adminInitScript = new File(FileHelper.homeDir(), "sql/tenant-schema-init-01.sql");
		adminExecutor.execute(adminInitScript);
	}
}