/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * OCR configuration parameters. Unused properties are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrParams {
	private String requestQueue;
	private String responseQueue;

	public String getRequestQueue() {
		return requestQueue;
	}

	public void setRequestQueue(String requestQueue) {
		this.requestQueue = requestQueue;
	}

	public String getResponseQueue() {
		return responseQueue;
	}

	public void setResponseQueue(String responseQueue) {
		this.responseQueue = responseQueue;
	}
}