/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Stores and retrieves extract definitions.
 */
public class ExtractFactory {

	// List of extract definitions.
	private static final TreeMap<String, Extract> extracts = new TreeMap<String, Extract>();
	{
		addExtract(new InvoiceExtract());
		addExtract(new InvoiceItemExtract());
	}

	private ExtractFactory() {
	}

	public static ExtractFactory getInstance() {
		return new ExtractFactory();
	}

	public void addExtract(Extract extract) {
		extracts.put(extract.name(), extract);
	}

	public Extract getExtract(String name) {
		return extracts.get(name);
	}

	public List<Extract> getAll() {
		List<Extract> extractList = new ArrayList<Extract>(extracts.size());
		for (Extract extract : extracts.values()) {
			extractList.add(extract);
		}
		return extractList;
	}
}