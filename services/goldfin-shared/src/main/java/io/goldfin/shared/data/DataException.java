/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * Denotes a DBMS error with root cause if available.
 */
public class DataException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataException(String msg) {
		super(msg);
	}

	public DataException(String msg, Throwable cause) {
		super(msg, cause);
	}
}