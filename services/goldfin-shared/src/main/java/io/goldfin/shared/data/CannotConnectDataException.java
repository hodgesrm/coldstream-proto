/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * A DBMS transaction failed because we could not connect to the DBMS.
 */
public class CannotConnectDataException extends DataException {
	private static final long serialVersionUID = 1L;

	public CannotConnectDataException() {
		super("Unable to connect to DBMS");
	}

	public CannotConnectDataException(String msg) {
		super(msg);
	}

	public CannotConnectDataException(String msg, Throwable cause) {
		super(msg, cause);
	}
}