/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.dbutils;

/**
 * Holds a SQL load failure with accompanying root cause if known.
 */
public class SqlLoadException extends Exception {
	private static final long serialVersionUID = 1L;

	public SqlLoadException(String msg) {
		super(msg);
	}

	public SqlLoadException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
