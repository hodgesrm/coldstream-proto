/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.data.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.utilities.YamlHelper;

/**
 * Verifies loading of SQL scripts. Test DBMS connection parameters must be
 * stored in src/test/resources/dbms.yaml.
 */
public class TransactionTest {
	static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

	private ConnectionParams testDbParams;
	private SimpleJdbcConnectionManager connectionManager;
	private TestDataService tds = new TestDataService();

	/** Load DBMS connection parameters. */
	@Before
	public void setup() throws Exception {
		testDbParams = YamlHelper.readFromClasspath("dbms.yaml", ConnectionParams.class);
		connectionManager = new SimpleJdbcConnectionManager(testDbParams);
	}

	/**
	 * Verify that we can build a session and commit.
	 */
	@Test
	public void testTransactionCommit() throws Exception {
		// Set up builder.
		SessionBuilder builder = new SessionBuilder().connectionManager(connectionManager).tenantType("test1")
				.addService(tds);

		// Set up date.
		Session session = builder.build();
		tds.setup();
		session.commit();

		// Now add data and commit.
		String id = tds.addValue("test data");
		session.commit();

		// Prove that data exist.
		String value = tds.getValue(id);
		session.commit();
		Assert.assertNotNull(value);
		Assert.assertEquals("test data", value);
	}

	/**
	 * Verify that rollback removes uncommitted changes.
	 */
	@Test
	public void testTransactionRollback() throws Exception {
		// Set up builder.
		SessionBuilder builder = new SessionBuilder().connectionManager(connectionManager).tenantType("test2")
				.addService(tds);

		// Set up date.
		Session session = builder.build();
		tds.setup();
		session.commit();

		// Now add one row and commit.
		String id1 = tds.addValue("test data 1");
		String id2 = tds.addValue("test data 2");
		session.commit();

		// Delete one value and show it is gone.
		tds.deleteValue("test data 2");
		String value = tds.getValue(id2);
		Assert.assertNull(value);

		// Roll back and show that the value returns.
		session.rollback();
		String value2 = tds.getValue(id2);
		Assert.assertNotNull(value2);
		Assert.assertEquals("test data 2", value2);
	}
}