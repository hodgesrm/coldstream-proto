/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * Denotes a service with operations that participate in DBMS transactions.
 */
public interface TransactionalService {
	public void setSession(Session session);
}