/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

/**
 * Denotes an error in a serialization or deserialization operation.
 */
public class SerializationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SerializationException(String msg) {
		super(msg);
	}

	public SerializationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}