/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.svc.test;

import java.util.concurrent.atomic.AtomicInteger;

import io.goldfin.shared.testing.DbConnectionHelper;
import io.goldfin.shared.testing.TransactionalTestHelper;

/**
 * Superclass for test generators that provides an index as well as connection helper. 
 */
public abstract class AdminTestGenerator<T> implements TransactionalTestHelper<T> {
	protected static AtomicInteger index = new AtomicInteger(1);
	protected DbConnectionHelper connectionHelper;

	public AdminTestGenerator(DbConnectionHelper connectionHelper) {
		this.connectionHelper = connectionHelper;
	}

	@Override
	public DbConnectionHelper connectionHelper() {
		return connectionHelper;
	}
}