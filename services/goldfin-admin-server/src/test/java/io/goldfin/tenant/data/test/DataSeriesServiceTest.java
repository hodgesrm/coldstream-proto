/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.DataSeries.FormatEnum;
import io.goldfin.admin.service.api.model.DataSeries.StateEnum;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.tenant.data.DataSeriesDataService;

/**
 * Implements a transactional service test on the data series service.
 */
public class DataSeriesServiceTest extends TransactionalTest<DataSeries> {
	static final Logger logger = LoggerFactory.getLogger(DataSeriesServiceTest.class);

	private static final String SCHEMA = "test_data_series";
	private static DbConnectionHelper globalConnectionHelper;

	class DataSeriesGenerator extends TenantTestGenerator<DataSeries> {

		public DataSeriesGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<DataSeries> service() {
			return new DataSeriesDataService();
		}

		@Override
		public DataSeries generate() {
			DataSeries ds = new DataSeries();
			ds.setId(UUID.randomUUID());
			ds.setName("somename");
			ds.setDescription("ABCDEF");
			ds.setContentType("application/octet-stream");
			ds.setContentLength(BigDecimal.valueOf(1000));
			ds.setThumbprint("xxxxx");
			ds.setLocator("some locator");
			ds.setState(StateEnum.CREATED);
			ds.setFormat(FormatEnum.UNKNOWN);
			return ds;
		}

		@Override
		public DataSeries mutate(DataSeries old) {
			DataSeries ds = new DataSeries();
			ds.setId(old.getId());
			ds.setDescription(old.getDescription().substring(0, 5) + "_" + index.incrementAndGet());
			return ds;
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
		this.testHelper = new DataSeriesGenerator(globalConnectionHelper);
	}
}