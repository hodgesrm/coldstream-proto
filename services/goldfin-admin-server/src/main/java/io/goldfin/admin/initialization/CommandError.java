/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

/**
 * Denotes a command error.
 */
public class CommandError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommandError(String msg) {
		super(msg);
	}

	public CommandError(String msg, Throwable cause) {
		super(msg, cause);
	}
}