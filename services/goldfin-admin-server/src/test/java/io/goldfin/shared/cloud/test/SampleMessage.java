/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud.test;

import java.util.Objects;

/**
 * Sample data for testing.
 */
public class SampleMessage {
	Integer intValue;
	String stringValue;

	public SampleMessage() {
	}
	
	public SampleMessage(Integer intValue, String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}
	
	public Integer getIntValue() {
		return intValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		SampleMessage other = (SampleMessage) o;
		return (Objects.equals(this.intValue, other.intValue) && (Objects.equals(this.stringValue, other.stringValue)));
	}
}