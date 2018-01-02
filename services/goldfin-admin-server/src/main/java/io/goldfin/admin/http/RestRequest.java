/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import io.goldfin.shared.utilities.JsonHelper;

/**
 * Basic REST request.
 */
public class RestRequest {
	RestHttpMethod method;
	String path;
	Map<String, String> headers = new HashMap<String, String>();
	HttpEntity httpEntity;
	MultipartEntityBuilder multipartBuilder;

	public RestRequest() {
	}

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

	/** Method to set binary content. This must be the only content operation. */
	public RestRequest content(byte[] content) {
		assertNoContent();
		httpEntity = new ByteArrayEntity(content);
		return this;
	}

	/** Method to set object content, which will be serialized to JSON. */
	public RestRequest content(Object o) {
		assertNoContent();
		httpEntity = new StringEntity(JsonHelper.writeToString(o), "UTF-8");
		header("Content-Type", "application/json");
		return this;
	}

	/** Method to start a multi-part request. */
	public RestRequest multipart() {
		assertNoContent();
		multipartBuilder = MultipartEntityBuilder.create();
		multipartBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		return this;
	}

	/** Add file to multipart request. */
	public RestRequest addFile(String name, File file) {
		assertMultipart();
		multipartBuilder.addBinaryBody(name, file, ContentType.DEFAULT_BINARY, file.getName());
		return this;
	}

	public RestRequest addText(String name, String value) {
		assertMultipart();
		multipartBuilder.addTextBody(name, value);
		return this;
	}

	public boolean hasHttpEntity() {
		return httpEntity != null || multipartBuilder != null;
	}

	public HttpEntity getHttpEntity() {
		if (httpEntity != null) {
			return httpEntity;
		} else if (multipartBuilder != null) {
			httpEntity = multipartBuilder.build();
			multipartBuilder = null;
			return httpEntity;
		} else {
			return null;
		}
	}

	private void assertNoContent() {
		if (this.httpEntity != null || this.multipartBuilder != null) {
			throw new UnsupportedOperationException("Content may not be set twice");
		}
	}

	private void assertMultipart() {
		if (this.multipartBuilder == null) {
			throw new UnsupportedOperationException("No multipart builder found");
		}
	}
}