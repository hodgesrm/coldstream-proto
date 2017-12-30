/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.initialization.SvcInit;
import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.DataException;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.utilities.FileHelper;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Implements a transactional service test on the user service.
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
		// Set up test directory & copy sample parameter file with substitutions.
		File testDir = FileHelper.resetDirectory(new File("target/testdata/serviceCreateRemove"));
		File sampleParamsFile = new File("conf/init-params.sample.yaml");
		SystemInitParams initParams = YamlHelper.readFromFile(sampleParamsFile, SystemInitParams.class);
		initParams.setServiceDb("goldfin_test");
		initParams.setServiceUser("goldfin_test");
		File initParamsFile = new File(testDir, "init-params.yaml");
		YamlHelper.writeToFile(initParamsFile, initParams);

		// Run remove pre-emptively, ignoring errors.
		SvcInit svctl = new SvcInit();
		String[] removeArgsNoErrors = { "remove", "--init-params", initParamsFile.getAbsolutePath(),
				"--ignore-errors" };
		svctl.run(removeArgsNoErrors);

		// Add service schema.
		File dbmsConfigFile = new File(testDir, "dbms.yaml");
		String[] createArgs = { "create", "--init-params", initParamsFile.getAbsolutePath(), "--dbms-config",
				dbmsConfigFile.getAbsolutePath() };
		svctl.run(createArgs);

		// Confirm we can connect to DBMS.
		ConnectionParams connectionParams = YamlHelper.readFromFile(dbmsConfigFile, ConnectionParams.class);
		Assert.assertTrue("Can connect to new schema", checkConnection(connectionParams));

		// Remove service schema.
		String[] removeArgs = { "remove", "--init-params", initParamsFile.getAbsolutePath() };
		svctl.run(removeArgs);

		// Confirm we can no longer connect using generated database parameters.
		Assert.assertFalse("Can connect to new schema", checkConnection(connectionParams));
	}

	// See whether we can connect to DBMS using supplied connection parameters.
	private boolean checkConnection(ConnectionParams connectionParams) {
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