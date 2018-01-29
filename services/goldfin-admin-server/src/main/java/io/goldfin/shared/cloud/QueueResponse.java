/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

/**
 * Wrapper for an incoming message object that includes the message key to delete
 * from queue.
 */
public class QueueResponse<T> {
	private final String key;
	private final T object;

	public QueueResponse(String key, T object) {
		this.key = key;
		this.object = object;
	}

	public String getKey() {
		return key;
	}

	public T getObject() {
		return object;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getSimpleName());
		buf.append(" key=").append(key);
		buf.append(", objectClass=").append(object.getClass().getSimpleName());
		return buf.toString();
	}
}