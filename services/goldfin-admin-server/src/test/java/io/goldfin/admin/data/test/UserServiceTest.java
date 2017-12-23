/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.test;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.UserData;
import io.goldfin.admin.data.UserDataService;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;

/**
 * Implements a transactional service test on the user service. 
 */
public class UserServiceTest extends TransactionalTest<UserData> {
	static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

	private static final String SCHEMA = "test_userRecord";
	private static DbConnectionHelper globalConnectionHelper;
	private static String testTenantId;

	class UserRecordGenerator extends AdminTestGenerator<UserData> {
		UUID tenantId;

		public UserRecordGenerator(DbConnectionHelper connectionHelper, String tenantIdAsString) {
			super(connectionHelper);
			this.tenantId = UUID.fromString(tenantIdAsString);
		}

		@Override
		public TransactionalService<UserData> service() {
			return new UserDataService();
		}

		@Override
		public UserData generate() {
			UserData ur = new UserData();
			ur.setId(UUID.randomUUID());
			ur.setTenantId(tenantId);
			ur.setUsername("username_" + index.getAndIncrement());
			ur.setRoles("admin");
			return ur;
		}

		@Override
		public UserData mutate(UserData old) {
			UserData ur = new UserData();
			ur.setId(old.getId());
			ur.setTenantId(old.getTenantId());
			ur.setUsername("username_" + index.getAndIncrement());
			ur.setRoles(old.getRoles());
			ur.setPasswordHash(old.getPasswordHash());
			ur.setAlgorithm(old.getAlgorithm());
			ur.setCreationDate(old.getCreationDate());
			return ur;
		}
	}

	/** Ensure SQL is loaded prior to running any test case. */
	@BeforeClass
	public static void setupAll() throws Exception {
		DbConnectionHelper dch = new DbConnectionHelper(SCHEMA);
		AdminTestHelper.loadAdminSchema(dch.getTestDbParams(), dch.getSchema());
		testTenantId = AdminTestHelper.createTenant(dch.getTestDbParams(), dch.getSchema(), "Test");
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new UserRecordGenerator(globalConnectionHelper, testTenantId);
	}
}