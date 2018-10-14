/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract.test;

import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.extract.Extract;

/**
 * Defines a sample extract for testing.
 */
public class SampleExtract implements Extract {
	public String name() {
		return "sample_extract";
	}

	public SqlSelect baseQuery() {
		return new SqlSelect().from("extract_table", "et").project("et.*").orderByAscending("id");
	}
}