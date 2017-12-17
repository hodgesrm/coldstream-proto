/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.testing;

import io.goldfin.shared.data.TransactionalService;

/**
 * Denotes a class that generates and mutates data for testing transactional
 * services.
 */
public interface TransactionalTestHelper<T> {
	/** Return connection helper. */
	public DbConnectionHelper connectionHelper();
	
	/** Generate a data service for the entity. */
	public TransactionalService<T> service();
	
	/** Generate a new entity. */
	public T generate();

	/** Mutate an existing entity in a legal way for update. */
	public T mutate(T old);
}