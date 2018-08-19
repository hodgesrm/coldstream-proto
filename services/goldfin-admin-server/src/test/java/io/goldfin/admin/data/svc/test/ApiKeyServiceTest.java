/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.svc.test;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.svc.ApiKeyData;
import io.goldfin.admin.data.svc.ApiKeyService;
import io.goldfin.shared.crypto.Randomizer;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;

/**
 * Implements a transactional service test on the API Key service.
 */
public class ApiKeyServiceTest extends TransactionalTest<ApiKeyData> {
	static final Logger logger = LoggerFactory.getLogger(ApiKeyServiceTest.class);

	private static final String SCHEMA = "test_userRecord";
	private static DbConnectionHelper globalConnectionHelper;
	private static String testTenantId;
	private static String testUserId;
	private static AtomicLong counter = new AtomicLong(0);

	class ApiKeyRecordGenerator extends AdminTestGenerator<ApiKeyData> {
		UUID tenantId;
		UUID userId;

		public ApiKeyRecordGenerator(DbConnectionHelper connectionHelper, String tenantIdAsString,
				String userIdAsString) {
			super(connectionHelper);
			this.tenantId = UUID.fromString(tenantIdAsString);
			this.userId = UUID.fromString(userIdAsString);
		}

		@Override
		public TransactionalService<ApiKeyData> service() {
			return new ApiKeyService();
		}

		@Override
		public ApiKeyData generate() {
			// Secret hash test value must be unique, so we use Randomizer class.
			ApiKeyData ur = new ApiKeyData();
			ur.setId(UUID.randomUUID());
			ur.setUserId(userId);
			ur.setName("Some name" + counter.getAndIncrement());
			ur.setSecretHash(new Randomizer().base64RandomBytes(20));
			ur.setAlgorithm("algo");
			ur.setLastTouchedDate(new Timestamp(System.currentTimeMillis()));
			return ur;
		}

		@Override
		public ApiKeyData mutate(ApiKeyData old) {
			ApiKeyData ur = new ApiKeyData();
			ur.setId(old.getId());
			ur.setUserId(old.getUserId());
			ur.setSecretHash(old.getSecretHash());
			ur.setAlgorithm(old.getAlgorithm());
			ur.setLastTouchedDate(new Timestamp(old.getLastTouchedDate().getTime() + 1));
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
		testUserId = AdminTestHelper.createUser(dch.getTestDbParams(), dch.getSchema(), testTenantId, "test_user");
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new ApiKeyRecordGenerator(globalConnectionHelper, testTenantId, testUserId);
	}
}