/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes a new invoice service. Initialization logic runs in a separate
 * thread and reports progress back to client if a progress reporter is
 * available.
 */
public class SystemInitializer implements ProgressReporter {
	static final Logger logger = LoggerFactory.getLogger(SystemInitializer.class);

	private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	private final List<ProgressReporter> progressReporters = new ArrayList<ProgressReporter>();
	private final SystemInitParams initParams;

	public SystemInitializer(SystemInitParams initParams) {
		this.initParams = initParams;
	}

	public SystemInitializer addProgressReporter(ProgressReporter reporter) {
		this.progressReporters.add(reporter);
		return this;
	}

	@Override
	public void progress(String message, BigDecimal percent) {
		for (ProgressReporter reporter: progressReporters) {
			reporter.progress(message, percent);
		}
	}

	public Future<TaskStatus> initialize() {
		SystemInitializationTask task = new SystemInitializationTask(initParams, this);
		return threadPool.submit(task);
	}
}