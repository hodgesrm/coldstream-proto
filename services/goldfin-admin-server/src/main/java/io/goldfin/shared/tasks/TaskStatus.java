/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.tasks;

/**
 * Initializes a new invoice service. Initialization logic runs in a separate
 * thread and reports progress back to client if a progress reporter is
 * available.
 */
public class TaskStatus {
	public enum Outcome {
		SUCCEEDED, FAILED, CANCELLED
	};

	private final Outcome outcome;
	private final String message;
	private final String task;
	private final Throwable throwable;

	private TaskStatus(Outcome outcome, String message, String task, Throwable throwable) {
		this.outcome = outcome;
		this.message = message;
		this.task = task;
		this.throwable = throwable;
	}

	public static TaskStatus successfulTask(String message, String task) {
		return new TaskStatus(Outcome.SUCCEEDED, message, task, null);
	}

	public static TaskStatus cancelledTask(String task) {
		return new TaskStatus(Outcome.CANCELLED, null, task, null);
	}

	public static TaskStatus failedTask(String message, String task, Throwable throwable) {
		return new TaskStatus(Outcome.FAILED, message, task, throwable);
	}

	public boolean succeeded() {
		return outcome == Outcome.SUCCEEDED;
	}

	public boolean failed() {
		return outcome == Outcome.FAILED;
	}

	public boolean cancelled() {
		return outcome == Outcome.CANCELLED;
	}

	/**
	 * @return the outcome
	 */
	public Outcome getOutcome() {
		return outcome;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}