/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.postgresql.util.PGobject;

/**
 * Represents data for a single SQL row. Column values are 1-based in good JDBC
 * tradition.
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
		Object o = get(index);
		if (o instanceof PGobject) {
			return ((PGobject) o).getValue();
		} else {
			return (String) o;
		}
	}

	public Integer getAsInt(int index) {
		return (Integer) get(index);
	}

	public Object get(String name) {
		return columnValues.get(name);
	}

	public String getAsString(String name) {
		Object o = get(name);
		if (o instanceof PGobject) {
			return ((PGobject) o).getValue();
		} else {
			return (String) o;
		}
	}

	public UUID getAsUUID(String name) {
		return (UUID) get(name);
	}

	public Integer getAsInt(String name) {
		return (Integer) get(name);
	}

	public Long getAsLong(String name) {
		return (Long) get(name);
	}

	public BigDecimal getAsBigDecimal(String name) {
		Object o = get(name);
		if (o instanceof Long) {
			return BigDecimal.valueOf((Long) o);
		} else {
			return (BigDecimal) get(name);
		}
	}

	public Timestamp getAsTimestamp(String name) {
		return (Timestamp) get(name);
	}

	public Date getAsDate(String name) {
		return (Date) get(name);
	}

	public java.util.Date getAsJavaDate(String name) {
		Object o = get(name);
		if (o == null) {
			return null;
		} else if (o instanceof Timestamp) {
			return new java.sql.Date(((Timestamp) o).getTime());
		} else if (o instanceof Date) {
			return (Date) o;
		} else if (o instanceof String) {
			return java.sql.Date.valueOf((String) o);
		} else {
			// Not sure what to do here, so we'll just lose data.
			return null;
		}
	}

	public Boolean getAsBoolean(String name) {
		return (Boolean) get(name);
	}
}