/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.exceptions.ServerException;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.extract.ExtractQuery;

/**
 * Processes generation of extracts from the DBMS.
 */
public class ExtractDataService {
	static private final Logger logger = LoggerFactory.getLogger(ExtractDataService.class);

	private final Session session;

	public ExtractDataService(Session session) {
		this.session = session;
	}

	/** Run query for data extract and return tabular result. */
	public TabularResultSet getExtract(String extractType, String filter, String order, int limit)
			throws InvalidInputException, ServerException {
		ExtractQuery eq = new ExtractQuery().name(extractType).sortOrder(order).limit(limit);
		SqlSelect select = eq.build(session);
		try {
			return select.run(session);
		} catch (Exception e) {
			logger.error("Query failed", e);
			throw new ServerException("Query failed", e);
		}
	}
}