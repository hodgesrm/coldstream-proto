/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Contains the result of a validation check on an invoice.
 */
public class ValidationResult {
	private String key;
	private String summary;
	private ValidationType validationType;
	private boolean passed;
	private String details;
	private String itemId;
	private String resourceId = null;

	public String getKey() {
		return key;
	}

	public ValidationResult setKey(String key) {
		this.key = key;
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public ValidationResult setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public ValidationType getValidationType() {
		return validationType;
	}

	public ValidationResult setValidationType(ValidationType validationType) {
		this.validationType = validationType;
		return this;
	}

	public boolean isPassed() {
		return passed;
	}

	public ValidationResult setPassed(boolean passed) {
		this.passed = passed;
		return this;
	}

	public String getDetails() {
		return details;
	}

	public ValidationResult setDetails(String details) {
		this.details = details;
		return this;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}