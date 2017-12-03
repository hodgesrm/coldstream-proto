/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.util.concurrent.Callable;

/**
 * Abstract task stub. 
 */
public abstract class AbstractTaskAdapter implements Callable<TaskStatus> {
	protected final ProgressReporter progressReporter;

	public AbstractTaskAdapter(ProgressReporter reporter) {
		this.progressReporter = reporter;
	}

	/**
	 * Implementors must define the task here. 
	 */
	@Override
	public abstract TaskStatus call(); 
}