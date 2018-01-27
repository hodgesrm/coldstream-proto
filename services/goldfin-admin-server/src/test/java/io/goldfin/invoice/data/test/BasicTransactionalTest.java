/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.data.test;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.shared.testing.TransactionalTestHelper;

/**
 * Runs the generic transaction test on a
 */
public class BasicTransactionalTest extends TransactionalTest<SampleEntity> {
	static final Logger logger = LoggerFactory.getLogger(BasicTransactionalTest.class);

	private static final String SCHEMA = "test_basic";
	private static DbConnectionHelper globalConnectionHelper;

	class BasicGenerator implements TransactionalTestHelper<SampleEntity> {
		private int index = 1;
		private DbConnectionHelper connectionHelper;

		public BasicGenerator(DbConnectionHelper connectionHelper) {
			this.connectionHelper = connectionHelper;
		}

		@Override
		public DbConnectionHelper connectionHelper() {
			return connectionHelper;
		}

		@Override
		public TransactionalService<SampleEntity> service() {
			return new TestDataService();
		}

		@Override
		public SampleEntity generate() {
			SampleEntity se = new SampleEntity();
			se.setId(UUID.randomUUID().toString());
			se.setValue("abdcefghi_" + index++);
			return se;
		}

		@Override
		public SampleEntity mutate(SampleEntity old) {
			SampleEntity se2 = new SampleEntity();
			se2.setValue(old.getValue().substring(0, 9) + index++);
			return se2;
		}
	}

	/** Ensure SQL is loaded prior to running any test case. */
	@BeforeClass
	public static void setupAll() throws Exception {
		DbConnectionHelper dch = new DbConnectionHelper(SCHEMA);

		// Load schema for the test.
		TestDataService tds = new TestDataService();
		Session session = new SessionBuilder().connectionManager(dch.getConnectionManager())
				.ensureSchema(dch.getSchema()).addService(tds).build();
		tds.setup();
		session.commit();
		session.close();

		// Store the connection helper for test case use.
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new BasicGenerator(globalConnectionHelper);
	}
}