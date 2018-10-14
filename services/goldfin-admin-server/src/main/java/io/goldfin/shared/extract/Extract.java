/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import io.goldfin.shared.data.SqlSelect;

/**
 * Implements an extract definition that specifies the name as well as fields and
 * also generates the base query for the extract.
 */
public interface Extract {
	/** Return the name. */
	public String name();

	/**
	 * Return the extract query.
	 */
	public SqlSelect baseQuery();
}