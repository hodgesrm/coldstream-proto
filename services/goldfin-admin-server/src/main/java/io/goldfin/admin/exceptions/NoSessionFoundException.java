/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

/**
 * Denotes an expired or missing session error. 
 */
public class NoSessionFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoSessionFoundException(String msg) {
		super(msg);
	}

	public NoSessionFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}