/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.data.test;

import java.util.Objects;

/**
 */
public class SampleEntity {
	private String id;
	private String value;

	public boolean equals(Object o) {
        if (o == null) {
        	return false;
        }
        else if (! (this.getClass() == o.getClass())) {
			return false;
		}
		SampleEntity other = (SampleEntity) o;
		if (! Objects.equals(this.id, other.id)) {
			return false;
		} else if (! Objects.equals(this.value, other.value)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
