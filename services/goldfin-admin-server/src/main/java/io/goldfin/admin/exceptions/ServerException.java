/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

/**
 * Denotes an exception due to a server processing failure.
 */
public class ServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServerException(String msg) {
		super(msg);
	}

	public ServerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}