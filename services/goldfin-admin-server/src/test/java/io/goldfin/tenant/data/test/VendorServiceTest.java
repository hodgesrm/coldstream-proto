/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.model.Vendor.StateEnum;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.tenant.data.VendorDataService;

/**
 * @author rhodges
 *
 */
public class VendorServiceTest extends TransactionalTest<Vendor> {
	static final Logger logger = LoggerFactory.getLogger(VendorServiceTest.class);

	private static final String SCHEMA = "test_vendor";
	private static DbConnectionHelper globalConnectionHelper;

	class VendorGenerator extends TenantTestGenerator<Vendor> {
		public VendorGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<Vendor> service() {
			return new VendorDataService();
		}

		@Override
		public Vendor generate() {
			Vendor t = new Vendor();
			t.setId(UUID.randomUUID());
			t.setIdentifier("VENDOR-" + index.getAndIncrement() + ".com");
			t.setName("vendor_" + index.getAndIncrement());
			t.setState(StateEnum.ACTIVE);
			return t;
		}

		@Override
		public Vendor mutate(Vendor old) {
			Vendor t2 = new Vendor();
			t2.setId(old.getId());
			t2.setIdentifier(old.getIdentifier());
			t2.setName(old.getName().substring(0, 7) + index.getAndIncrement());
			t2.setState(old.getState());
			t2.setCreationDate(old.getCreationDate());
			return t2;
		}
	}

	/** Ensure SQL is loaded prior to running any test case. */
	@BeforeClass
	public static void setupAll() throws Exception {
		DbConnectionHelper dch = new DbConnectionHelper(SCHEMA);
		TenantTestHelper.loadTenantSchema(dch.getTestDbParams(), dch.getSchema());
		globalConnectionHelper = dch;
	}

	@Before
	public void setup() {
		this.testHelper = new VendorGenerator(globalConnectionHelper);
	}
}