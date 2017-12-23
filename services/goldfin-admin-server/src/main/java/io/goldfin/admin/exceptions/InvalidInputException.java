/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

/**
 * Denotes an exception due to invalid input to a transaction. 
 */
public class InvalidInputException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String msg) {
		super(msg);
	}

	public InvalidInputException(String msg, Throwable cause) {
		super(msg, cause);
	}
}