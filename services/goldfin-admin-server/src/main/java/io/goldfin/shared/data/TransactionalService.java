/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.util.List;

/**
 * Denotes a service with standard CRUD operations that participate in DBMS
 * transactions. Implementations may include additional operations of course.
 */
public interface TransactionalService<T> {
	public void setSession(Session session);

	/** Store model in DBMS returning an ID. */
	public String create(T model);

	/** Update existing entity in DBMS to be like the model. */
	public int update(String id, T model);

	/** Delete existing entity in DBMS. */
	public int delete(String id);

	/** Get a single entity or return null if not found. */
	public T get(String id);

	/** Return a list of all entities of type T. */
	public List<T> getAll();
}