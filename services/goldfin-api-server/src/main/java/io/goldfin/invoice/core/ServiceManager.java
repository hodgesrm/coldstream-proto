/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for Goldfin service creation and deletion.
 */
public class ServiceManager implements ProgressReporter {
	static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

	private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	private final List<ProgressReporter> progressReporters = new ArrayList<ProgressReporter>();
	private final SystemInitParams initParams;

	public ServiceManager(SystemInitParams initParams) {
		this.initParams = initParams;
	}

	public ServiceManager addProgressReporter(ProgressReporter reporter) {
		this.progressReporters.add(reporter);
		return this;
	}

	@Override
	public void progress(String message, double percent) {
		for (ProgressReporter reporter: progressReporters) {
			reporter.progress(message, percent);
		}
	}

	/** Initialize a new service. */
	public Future<TaskStatus> create() {
		ServiceCreateTask task = new ServiceCreateTask(initParams, this);
		return threadPool.submit(task);
	}
	
	/** Remove an existing service. */
	public Future<TaskStatus> remove() {
		ServiceDeleteTask task = new ServiceDeleteTask(initParams, this);
		return threadPool.submit(task);
	}
}