/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data.test;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.model.Host.HostTypeEnum;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTest;
import io.goldfin.tenant.data.HostDataService;

/**
 * Implements a transactional service test on the host service.
 */
public class HostServiceTest extends TransactionalTest<Host> {
	static final Logger logger = LoggerFactory.getLogger(HostServiceTest.class);

	private static final String SCHEMA = "test_host";
	private static DbConnectionHelper globalConnectionHelper;

	class HostGenerator extends TenantTestGenerator<Host> {

		public HostGenerator(DbConnectionHelper connectionHelper) {
			super(connectionHelper);
		}

		@Override
		public TransactionalService<Host> service() {
			return new HostDataService();
		}

		@Override
		public Host generate() {
			Host host = new Host();
			host.setId(UUID.randomUUID());
			host.setHostId("2031524");
			host.setResourceId("MNQR001");
			host.setEffectiveDate(new Timestamp(System.currentTimeMillis()));
			host.setVendorIdentifier("prime-hosting");
			//host.setDataSeriesId(dataSeriesId);
			host.setHostType(HostTypeEnum.BARE_METAL);
			host.setHostModel("Basic");
			host.setRegion("US-EAST");
			host.setZone("1A");
			host.setDatacenter("EDC2");
			host.setCpu("Intel Dual-Core Pentium G6950");
			host.setSocketCount(1);
			host.setCoreCount(2);
			host.setThreadCount(1);
			host.setRam(4 * 1024 * 1024 * 1024L);
			host.setHdd(512 * 1024 * 1024 * 1024L);
			host.setSsd(512 * 1024 * 1024 * 1024L);
			host.setNicCount(2);
			host.setNetworkTrafficLimit(10 * 1024 * 1024 * 1024L);
			host.setBackupEnabled(true);
			
			return host;
		}

		@Override
		public Host mutate(Host old) {
			throw new UnsupportedOperationException("Hosts are immutable!");
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
		this.testHelper = new HostGenerator(globalConnectionHelper);
	}
}