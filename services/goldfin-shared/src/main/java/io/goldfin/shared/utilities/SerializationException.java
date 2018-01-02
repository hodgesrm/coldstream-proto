/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

/**
 * Indicates an error in Jackson serialization/deserialization.
 */
public class SerializationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SerializationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
