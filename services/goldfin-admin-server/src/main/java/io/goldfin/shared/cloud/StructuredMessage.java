/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.util.HashMap;
import java.util.Map;

import io.goldfin.shared.utilities.JsonHelper;

/**
 * Message that is sent or received on a queue.
 */
public class StructuredMessage {
	// Header names.
	public static String OPERATION = "operation";
	public static String TYPE = "type";
	public static String XACT_TAG = "xact_tag";
	public static String TENANT_ID = "tenant_id";
	public static String CONTENT_CLASS = "content_class";

	// Message headers and content.
	Map<String, String> headers = new HashMap<String, String>();
	private String content;
	private String receiptHandle;

	public Map<String, String> getHeaders() {
		return headers;
	}

	public StructuredMessage setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public String getOperation() {
		return headers.get(OPERATION);
	}

	public StructuredMessage setOperation(String operation) {
		headers.put(OPERATION, operation);
		return this;
	}

	public String getType() {
		return headers.get(TYPE);
	}

	public StructuredMessage setType(String type) {
		headers.put(TYPE, type);
		return this;
	}

	public String getXactTag() {
		return headers.get(XACT_TAG);
	}

	public StructuredMessage setXactTag(String xactTag) {
		headers.put(XACT_TAG, xactTag);
		return this;
	}

	public String getTenantId() {
		return headers.get(TENANT_ID);
	}

	public StructuredMessage setTenantId(String tenantId) {
		headers.put(TENANT_ID, tenantId);
		return this;
	}

	public String getContentClass() {
		return headers.get(CONTENT_CLASS);
	}

	public StructuredMessage setContentClass(String contentClass) {
		headers.put(CONTENT_CLASS, contentClass);
		return this;
	}

	public String getContent() {
		return content;
	}

	public StructuredMessage setContent(String content) {
		this.content = content;
		return this;
	}

	public StructuredMessage encodeContent(Object o) {
		content = JsonHelper.writeToString(o);
		setContentClass(o.getClass().getSimpleName());
		return this;
	}

	public <T> T decodeContent(Class<T> objectType) {
		return JsonHelper.readFromString(content, objectType);
	}

	public String getReceiptHandle() {
		return receiptHandle;
	}

	public StructuredMessage setReceiptHandle(String receiptHandle) {
		this.receiptHandle = receiptHandle;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getSimpleName());
		buf.append(" operation=").append(getOperation());
		buf.append(" type=").append(getType());
		buf.append(" xactTag=").append(getXactTag());
		buf.append(" tenantId=").append(getTenantId());
		buf.append(" contentClass=").append(getContentClass());
		if (content != null && content.length() > 50) {
			buf.append(String.format(" content=[%s...]", content.substring(0, 49)));
		} else {
			buf.append(String.format(" content=[%s]", content));
		}
		buf.append(" receiptHandle=").append(getReceiptHandle());
		return buf.toString();
	}
}