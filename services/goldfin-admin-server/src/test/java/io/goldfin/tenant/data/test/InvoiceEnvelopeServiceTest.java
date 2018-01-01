/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelope.StateEnum;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.tenant.data.InvoiceEnvelopeDataService;

/**
 * Implements a transactional service test on the session service.
 */
public class InvoiceEnvelopeServiceTest extends TransactionalTest<InvoiceEnvelope> {
	static final Logger logger = LoggerFactory.getLogger(InvoiceEnvelopeServiceTest.class);

	private static final String SCHEMA = "test_invoice_envelopes";
	private static DbConnectionHelper globalConnectionHelper;

	class InvoiceEnvelopeGenerator extends TenantTestGenerator<InvoiceEnvelope> {

		public InvoiceEnvelopeGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<InvoiceEnvelope> service() {
			return new InvoiceEnvelopeDataService();
		}

		@Override
		public InvoiceEnvelope generate() {
			InvoiceEnvelope env = new InvoiceEnvelope();
			env.setId(UUID.randomUUID());
			env.setDescription("some desc");
			env.setTags("tags");
			env.setState(StateEnum.CREATED);
			return env;
		}

		@Override
		public InvoiceEnvelope mutate(InvoiceEnvelope old) {
			InvoiceEnvelope env = new InvoiceEnvelope();
			env.setId(old.getId());
			env.setDescription(old.getDescription());
			env.setState(old.getState());
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
		this.testHelper = new InvoiceEnvelopeGenerator(globalConnectionHelper);
	}
}