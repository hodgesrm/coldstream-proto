/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

/**
 * Denotes a rest call failure.
 */
public class RestException extends Exception {
	private static final long serialVersionUID = 1L;

	public RestException(int status, String reason, String message) {
		super(String.format("Status=%d Reason=%s Message=%s", status, reason, message));
	}
}