/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import java.util.List;

import io.goldfin.shared.data.SqlSelect;

/**
 * Implements an extract definition that specifies the name as well as fields and
 * also generates the base query for the extract.
 */
public abstract class ExtractDefinition {
	protected final String name;
	protected final List<String> fields;

	public ExtractDefinition(String name, List<String> fields) {
		this.name = name;
		this.fields = fields;
	}

	/** Create extract definition from a model class that provides fields. */
	public ExtractDefinition(String name, Class<?> modelClass) {
		this.name = name;
		this.fields = inferFields(modelClass);
	}

	protected List<String> inferFields(Class<?> modelClass) {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getFields() {
		return this.fields;
	}

	/** Return the base query. */
	public abstract SqlSelect baseQuery();
}