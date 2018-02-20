/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.test;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.TenantDataService;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;

/**
 * @author rhodges
 *
 */
public class TenantServiceTest extends TransactionalTest<Tenant> {
	static final Logger logger = LoggerFactory.getLogger(TenantServiceTest.class);

	private static final String SCHEMA = "test_tenant";
	private static DbConnectionHelper globalConnectionHelper;

	class TenantGenerator extends AdminTestGenerator<Tenant> {
		public TenantGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<Tenant> service() {
			return new TenantDataService();
		}

		@Override
		public Tenant generate() {
			Tenant t = new Tenant();
			t.setId(UUID.randomUUID());
			t.setName("tenant_" + index.get());
			t.setSchemaSuffix(t.getName());
			t.setDescription("description_" + index.getAndIncrement());
			return t;
		}

		@Override
		public Tenant mutate(Tenant old) {
			Tenant t2 = new Tenant();
			t2.setId(old.getId());
			t2.setName(old.getName().substring(0, 7) + index.get());
			t2.setDescription(old.getDescription().substring(0, 11) + index.getAndIncrement());
			t2.setState(old.getState());
			t2.setSchemaSuffix(old.getSchemaSuffix());
			t2.setCreationDate(old.getCreationDate());
			return t2;
		}
	}

	/** Ensure SQL is loaded prior to running any test case. */
	@BeforeClass
	public static void setupAll() throws Exception {
		DbConnectionHelper dch = new DbConnectionHelper(SCHEMA);
		AdminTestHelper.loadAdminSchema(dch.getTestDbParams(), dch.getSchema());
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new TenantGenerator(globalConnectionHelper);
	}
}