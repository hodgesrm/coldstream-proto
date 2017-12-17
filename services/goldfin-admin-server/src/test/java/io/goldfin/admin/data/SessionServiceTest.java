/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;

/**
 * Implements a transactional service test on the session service.
 */
public class SessionServiceTest extends TransactionalTest<SessionData> {
	static final Logger logger = LoggerFactory.getLogger(SessionServiceTest.class);

	private static final String SCHEMA = "test_sessions";
	private static DbConnectionHelper globalConnectionHelper;
	private static String testTenantId;
	private static String testUserId;

	class SessionRecordGenerator extends AdminTestGenerator<SessionData> {
		UUID tenantId;
		UUID userId;

		public SessionRecordGenerator(DbConnectionHelper connectionHelper, String tenantIdAsString,
				String userIdAsString) {
			super(connectionHelper);
			this.tenantId = UUID.fromString(tenantIdAsString);
			this.userId = UUID.fromString(userIdAsString);
		}

		@Override
		public TransactionalService<SessionData> service() {
			return new SessionDataService();
		}

		@Override
		public SessionData generate() {
			SessionData ur = new SessionData();
			ur.setId(UUID.randomUUID());
			ur.setUserId(userId);
			ur.setToken(UUID.randomUUID().toString());
			return ur;
		}

		@Override
		public SessionData mutate(SessionData old) {
			SessionData sd = new SessionData();
			sd.setId(old.getId());
			sd.setUserId(old.getUserId());
			sd.setToken(old.getToken());
			sd.setLastTouched(old.getLastTouched());
			sd.setCreationDate(old.getCreationDate());
			return sd;
		}
	}

	/** Ensure SQL is loaded prior to running any test case. */
	@BeforeClass
	public static void setupAll() throws Exception {
		DbConnectionHelper dch = new DbConnectionHelper(SCHEMA);
		AdminTestHelper.loadAdminSchema(dch.getTestDbParams(), dch.getSchema());
		testTenantId = AdminTestHelper.createTenant(dch.getTestDbParams(), dch.getSchema(), "Test");
		testUserId = AdminTestHelper.createUser(dch.getTestDbParams(), dch.getSchema(), testTenantId, "test_user");
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new SessionRecordGenerator(globalConnectionHelper, testTenantId, testUserId);
	}
}