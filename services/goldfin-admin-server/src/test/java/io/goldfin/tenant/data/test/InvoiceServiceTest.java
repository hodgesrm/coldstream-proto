/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.tenant.data.InvoiceDataService;

/**
 * Implements a transactional service test on the session service.
 */
public class InvoiceServiceTest extends TransactionalTest<Invoice> {
	static final Logger logger = LoggerFactory.getLogger(InvoiceServiceTest.class);

	private static final String SCHEMA = "test_invoices";
	private static DbConnectionHelper globalConnectionHelper;

	class InvoiceGenerator extends TenantTestGenerator<Invoice> {

		public InvoiceGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<Invoice> service() {
			return new InvoiceDataService();
		}

		@Override
		public Invoice generate() {
			Invoice env = new Invoice();
			env.setId(UUID.randomUUID());
			//env.setDocumentId(UUID.randomUUID());
			env.setDescription("X");
			env.setTags("tags");
			env.setEffectiveDate(Date.valueOf("2016-12-01"));
			env.setVendorIdentifier("X");
			env.setSubtotalAmount(BigDecimal.valueOf(2000.0));
			env.setTax(BigDecimal.valueOf(100.0));
			env.setTotalAmount(BigDecimal.valueOf(210000, 2));
			env.setCurrency("USD");
			return env;
		}

		@Override
		public Invoice mutate(Invoice old) {
			Invoice env = new Invoice();
			env.setId(old.getId());
			env.setDescription(old.getDescription());
			env.setTags(old.getTags().substring(0, 4) + "_" + index.incrementAndGet());
			return env;
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
		this.testHelper = new InvoiceGenerator(globalConnectionHelper);
	}
}