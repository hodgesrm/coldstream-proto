/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

/**
 * Denotes a manager that services API requests. Managers are free to adopt any
 * API that is convenient provided calls are fully independent from each other
 * and thread-safe.
 */
public interface Manager {
	/** Supply service configuration. */
	public void setContext(ManagementContext context);

	/** Prepare the manager to receive calls. */
	public void prepare();

	/** Shut down the manager and deallocate resources. */
	public void release();
}