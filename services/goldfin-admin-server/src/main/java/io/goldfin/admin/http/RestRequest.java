/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import io.goldfin.shared.utilities.JsonHelper;

/**
 * Basic REST request.
 */
public class RestRequest {
	RestHttpMethod method;
	String path;
	Map<String, String> headers = new HashMap<String, String>();
	byte[] contentBytes;
	String contentString;
	String contentType;

	public RestRequest method(RestHttpMethod method) {
		this.method = method;
		return this;
	}

	public RestRequest POST() {
		method(RestHttpMethod.POST);
		return this;
	}

	public RestRequest GET() {
		method(RestHttpMethod.GET);
		return this;
	}

	public RestRequest DELETE() {
		method(RestHttpMethod.DELETE);
		return this;
	}

	public RestRequest path(String path) {
		this.path = path;
		return this;
	}

	public RestRequest header(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public RestRequest content(byte[] content) {
		this.contentBytes = content;
		return this;
	}

	public RestRequest content(Object o) {
		try {
			this.contentString = JsonHelper.writeToString(o);
			return contentType("application/json");
		} catch (IOException e) {
			throw new RestRuntimeException("Unable to serialize object to JSON: " + o.getClass().getName(), e);
		}
	}

	public RestRequest contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public boolean hasHttpEntity() {
		return contentString != null || contentBytes != null;
	}
	
	public HttpEntity getHttpEntity() {
		if (contentString != null) {
			return new StringEntity(contentString, "UTF-8");
		} else if (contentBytes != null) {
			return new ByteArrayEntity(contentBytes);
		} else {
			return null;
		}
	}
}