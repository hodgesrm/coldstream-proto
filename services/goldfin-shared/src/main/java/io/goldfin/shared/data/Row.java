/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents data for a single SQL row.
 */
public class Row {
	private final List<String> columnNames;
	private final Map<String, Object> columnValues;

	public Row(List<String> names, List<Object> values) {
		this.columnNames = names;
		this.columnValues = new HashMap<String, Object>();
		for (int i = 0; i < columnNames.size(); i++) {
			columnValues.put(names.get(i), values.get(i));
		}
	}

	public Object get(int index) {
		return columnValues.get(columnNames.get(index - 1));
	}

	public String getAsString(int index) {
		return (String) get(index);
	}

	public Integer getAsInt(int index) {
		return (Integer) get(index);
	}

	public Object get(String name) {
		return columnValues.get(name);
	}

	public String getAsString(String name) {
		return (String) get(name);
	}

	public UUID getAsUUID(String name) {
		return (UUID) get(name);
	}

	public Integer getAsInt(String name) {
		return (Integer) get(name);
	}

	public Timestamp getAsTimestamp(String name) {
		return (Timestamp) get(name);
	}
}
