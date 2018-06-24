/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.util.List;

import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Denotes a class that implements a validation for a particular entity type.
 */
public interface Rule<T> {
	/** Return the unique key of this rule. */
	public String getKey();

	/** Return the summary of the rule. */
	public String getSummary();

	/** Return the validate type that this rule implements. */
	public ValidationType getValidationType();

	/** Execute rule and return one or more results. */
	public List<ValidationResult> validate(T entity);
}