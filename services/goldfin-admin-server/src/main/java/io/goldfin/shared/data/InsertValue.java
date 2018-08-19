/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * Value holder for query parameters that will be inserted/updated with flag for
 * JSON types.
 */
public class InsertValue {
	String name;
	Object value;
	boolean json;
}