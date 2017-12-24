/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

/**
 * Denotes an exception that arises when an entity does not exist
 */
public class EntityNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException() {
		super("Entity not found");
	}

	public EntityNotFoundException(String msg) {
		super(msg);
	}

	public EntityNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}