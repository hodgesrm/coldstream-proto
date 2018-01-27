/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * A DBMS transaction failed because an expected entity was missing. 
 */
public class NotFoundDataException extends DataException {
	private static final long serialVersionUID = 1L;

	public NotFoundDataException() {
		super("Entity not found");
	}
	public NotFoundDataException(String msg) {
		super(msg);
	}
}