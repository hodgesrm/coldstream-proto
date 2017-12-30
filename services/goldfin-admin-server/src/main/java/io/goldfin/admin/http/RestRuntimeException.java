/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

/**
 * Denotes a REST API failure for reasons other than a call rejected by the REST
 * server.
 */
public class RestRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RestRuntimeException(String msg) {
		super(msg);
	}

	public RestRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
