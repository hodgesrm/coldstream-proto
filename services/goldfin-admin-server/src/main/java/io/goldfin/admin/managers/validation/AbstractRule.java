/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.util.List;

import io.goldfin.admin.service.api.model.ValidationType;

/**
 * Abstract super class for all rules that includes a nice toString() for
 * debugging.
 */
public abstract class AbstractRule<T> implements Rule<T> {
	public final String key;
	public final String summary;
	public final ValidationType validationType;

	public AbstractRule(String key, String summary, ValidationType validationType) {
		this.key = key;
		this.summary = summary;
		this.validationType = validationType;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public ValidationType getValidationType() {
		return validationType;
	}

	@Override
	public abstract List<ValidationResult> validate(T entity);

	@Override
	public String toString() {
		return String.format("%s - %s [%s] [%s]", this.getClass().getSimpleName(), key, validationType.toString(),
				summary);
	}

	/** Create partially filled in validation result for this rule. */
	public ValidationResult createValidationResult() {
		return new ValidationResult().setKey(key).setSummary(summary).setValidationType(validationType);
	}
}