/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.extract.ExtractDefinition;
import io.goldfin.shared.extract.InvoiceExtract;
import io.goldfin.shared.extract.InvoiceItemExtract;

/**
 * Processes generation of requests from the DBMS. 
 */
public class ExtractDataService {
	static private final Logger logger = LoggerFactory.getLogger(ExtractDataService.class);

	private Session session;

	// List of extract definitions. 
	private static final Map<String, ExtractDefinition> extracts = new HashMap<String, ExtractDefinition>();
	{
		addExtract(new InvoiceExtract());
		addExtract(new InvoiceItemExtract());
	}

	private static void addExtract(ExtractDefinition extract) {
		extracts.put(extract.getName(), extract);
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	/** Run query for data extract and result tabular result. */ 
	public TabularResultSet getExtract(String extractType, String filter) throws Exception {
		ExtractDefinition extract = extracts.get(extractType);
		if (extract == null) {
			throw new InvalidInputException("Unknown extract type: " + extractType);
		}
		
		SqlSelect select = extract.baseQuery();
		
		try {
			return select.run(session);
		} catch (Exception e) {
			logger.error("Query failed", e);
			throw new Exception("Query failed");
		}
	}
}