/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

/**
 * Denotes an expired or missing session error.
 */
public class UnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		super("Unauthorized");
	}

	public UnauthorizedException(String msg) {
		super(msg);
	}

	public UnauthorizedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}