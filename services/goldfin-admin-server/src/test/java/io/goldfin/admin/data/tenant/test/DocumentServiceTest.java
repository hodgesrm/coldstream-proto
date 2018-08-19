/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant.test;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.tenant.DocumentDataService;
import io.goldfin.admin.data.tenant.ExtendedTagSet;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Document.SemanticTypeEnum;
import io.goldfin.admin.service.api.model.Document.StateEnum;
import io.goldfin.admin.service.api.model.Tag;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;

/**
 * Implements a transactional service test on the session service.
 */
public class DocumentServiceTest extends TransactionalTest<Document> {
	static final Logger logger = LoggerFactory.getLogger(DocumentServiceTest.class);

	private static final String SCHEMA = "test_documents";
	private static DbConnectionHelper globalConnectionHelper;

	class DocumentGenerator extends TenantTestGenerator<Document> {

		public DocumentGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<Document> service() {
			return new DocumentDataService();
		}

		@Override
		public Document generate() {
			Document env = new Document();
			env.setId(UUID.randomUUID());
			env.setName("somename");
			env.setDescription("X");
			env.setContentType("application/octet-stream");
			env.setContentLength(BigDecimal.valueOf(1000));
			env.setThumbprint("xxxxx");
			env.setLocator("some locator");
			env.setState(StateEnum.CREATED);
			env.setSemanticType(SemanticTypeEnum.UNKNOWN);
			env.setSemanticId(UUID.randomUUID());
			env.setTags(
					new ExtendedTagSet().append(new Tag().name("a").value("1")).append(new Tag().name("b").value("2")));
			return env;
		}

		@Override
		public Document mutate(Document old) {
			Document doc = new Document();
			doc.setId(old.getId());
			doc.setDescription(old.getDescription());
			ExtendedTagSet ts = ((ExtendedTagSet) old.getTags()).clone();
			String newA = ts.tagGet("a") + "_" + index.incrementAndGet();
			ts.tagPut("a", newA);
			doc.setTags(ts);
			return doc;
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
		this.testHelper = new DocumentGenerator(globalConnectionHelper);
	}
}