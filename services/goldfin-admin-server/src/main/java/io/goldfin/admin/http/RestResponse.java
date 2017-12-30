/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic REST response.
 */
public class RestResponse {
	int code;
	String reason;
	Map<String, String> headers = new HashMap<String, String>();
	byte[] content;
	long contentLength;
	String contentType;

	public RestResponse code(int code) {
		this.code = code;
		return this;
	}

	public int getCode() {
		return code;
	}

	public boolean isError() {
		return code >= 300;
	}

	public RestResponse reason(String reason) {
		this.reason = reason;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public String getHeader(String key) {
		return headers.get(key);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public RestResponse content(byte[] content) {
		this.content = content;
		return this;
	}

	public byte[] getContent() {
		return content;
	}

	public RestResponse contentLength(long contentLength) {
		this.contentLength = contentLength;
		return this;
	}

	public long getContentLength() {
		return contentLength;
	}

	public RestResponse contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String getContentType() {
		return contentType;
	}
}