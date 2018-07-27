/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.initialization.SvcInit;
import io.goldfin.shared.config.ServiceConfig;
import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.data.DataException;
import io.goldfin.shared.data.DbmsParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.utilities.FileHelper;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Implements a test on functions to create and remove services. This test reads
 * in the base files and writes them with modifications to a test location.
 */
public class ServiceCtlTest {
	static final Logger logger = LoggerFactory.getLogger(ServiceCtlTest.class);

	@Before
	public void setup() {
	}

	/**
	 * Verify that we can call ServiceCtl to initialize and remove the entire
	 * service.
	 */
	@Test
	public void testCreateRemove() throws Exception {
		// Set up test directory & copy base parameter files with substitutions.
		File testDir = FileHelper.resetDirectory(new File("target/testdata/serviceCreateRemove"));
		File baseInitParamsFile = FileHelper.getConfigFile("init-params.yaml");
		Assert.assertNotNull(baseInitParamsFile);
		SystemInitParams initParams = YamlHelper.readFromFile(baseInitParamsFile, SystemInitParams.class);
		File initParamsFile = new File(testDir, "init-params.yaml");
		YamlHelper.writeToFile(initParamsFile, initParams);

		File baseServiceConfigFile = FileHelper.getConfigFile("service.yaml");
		ServiceConfig serviceConfig = YamlHelper.readFromFile(baseServiceConfigFile, ServiceConfig.class);
		Assert.assertNotNull(baseServiceConfigFile);
		serviceConfig.getDbms().setUser("goldfin_test");
		File serviceConfigFile = new File(testDir, "service.yaml");
		YamlHelper.writeToFile(serviceConfigFile, serviceConfig);

		// Run remove pre-emptively, ignoring errors.
		SvcInit svctl = new SvcInit();
		String[] removeArgsNoErrors = { "remove", "--init-params", initParamsFile.getAbsolutePath(), "--service-config",
				serviceConfigFile.getAbsolutePath(), "--ignore-errors" };
		svctl.run(removeArgsNoErrors);

		// Add service schema.
		String[] createArgs = { "create", "--init-params", initParamsFile.getAbsolutePath(), "--service-config",
				serviceConfigFile.getAbsolutePath() };
		svctl.run(createArgs);

		// Confirm we can connect to DBMS.
		Assert.assertTrue("Can connect to new schema", checkConnection(serviceConfig.getDbms()));

		// Remove service schema.
		String[] removeArgs = { "remove", "--init-params", initParamsFile.getAbsolutePath(), "--service-config",
				serviceConfigFile.getAbsolutePath() };
		svctl.run(removeArgs);

		// Confirm we can no longer connect using generated database parameters.
		Assert.assertFalse("Can connect to new schema", checkConnection(serviceConfig.getDbms()));
	}

	// See whether we can connect to DBMS using supplied connection parameters.
	private boolean checkConnection(DbmsParams connectionParams) {
		try {
			SimpleJdbcConnectionManager cm = new SimpleJdbcConnectionManager(connectionParams);
			Session session = new SessionBuilder().connectionManager(cm).useSchema(connectionParams.getAdminSchema())
					.build();
			session.close();
			logger.info("Able to connect to admin schema");
			return true;
		} catch (DataException e) {
			logger.info("Unable to connect to admin schema");
			return false;
		}
	}
}