/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.extract;

import java.util.ArrayList;
import java.util.List;

import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.shared.data.OrderByExpression;
import io.goldfin.shared.data.Ordering;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;

/**
 * Creates an extract query from an extract request.
 */
public class ExtractQuery {
	private String name;
	private int limit = -1;
	private List<OrderByExpression> orderByList = new ArrayList<OrderByExpression>();

	public ExtractQuery() {
	}

	public ExtractQuery name(String name) {
		ExtractFactory factory = ExtractFactory.getInstance();
		if (factory.getExtract(name) == null) {
			throw new InvalidInputException(String.format("Unknown extract name: %s", name));
		}
		this.name = name;
		return this;
	}

	public ExtractQuery limit(int limit) {
		this.limit = limit;
		return this;
	}

	public ExtractQuery sortOrder(String orderSpecificationList) throws InvalidInputException {
		// Convert the order to a normalized string with no whitespace. (Handles nulls,
		// too.)
		orderSpecificationList = normalizeWhiteSpace(orderSpecificationList);
		if (orderSpecificationList.length() > 0) {
			String[] orderClauses = orderSpecificationList.split(",");
			for (String orderClause : orderClauses) {
				String[] parts = orderClause.split(":");
				if (parts.length == 0) {
					continue;
				}
				OrderByExpression orderByExpr = new OrderByExpression();
				if ("".equals(parts[0])) {
					throw new InvalidInputException(
							String.format("Sort expression must have a column name: %s", orderClause));
				}

				if (parts.length == 1) {
					orderByExpr.name = parts[0];
					orderByExpr.order = Ordering.ASC;
				} else if (parts.length == 2) {
					orderByExpr.name = parts[0];
					if ("ASC".equals(parts[1].toUpperCase())) {
						orderByExpr.order = Ordering.ASC;
					} else if ("DESC".equals(parts[1].toUpperCase())) {
						orderByExpr.order = Ordering.DESC;
					} else {
						throw new InvalidInputException(
								String.format("Sort order must be asc or desc: %s", orderClause));
					}
				} else {
					throw new InvalidInputException(
							String.format("Sort order must have form <column>:[{asc|desc}]: %s", orderClause));
				}
				orderByList.add(orderByExpr);
			}
		}
		return this;
	}

	/**
	 * Create query for data extract. Checks column names using an advance query to
	 * prevent SQL injection attacks.
	 * 
	 * @param session
	 *            Session instance to query column metadata
	 * @return A SqlSelect that can be executed as a normal query
	 * @throws InvalidInputException
	 *             Thrown if the query has invalid columns
	 */
	public SqlSelect build(Session session) throws InvalidInputException {
		// Get the base query and extract column names.
		Extract extract = ExtractFactory.getInstance().getExtract(name);
		if (extract == null) {
			throw new InvalidInputException("Unknown extract: " + name);
		}

		// Run an empty query to get columnNames.
		TabularResultSet emptySet = extract.baseQuery().where("0 = 1", new Object[0]).run(session);
		List<String> columnNames = emptySet.columnNames();

		// Now prepare the real query.
		SqlSelect query = extract.baseQuery();

		// Process order by expressions and check that column names exist to
		// prevent SQL injection attacks. If there are no such expressions
		// we retain the default extract ordering.
		if (orderByList.size() > 0) {
			query.clearOrderBy();
			for (OrderByExpression orderBy : orderByList) {
				if (columnNames.contains(orderBy.name)) {
					query.orderBy(orderBy.name, orderBy.order);
				} else {
					throw new InvalidInputException(String.format("Unknown column name in order by: %s", orderBy.name));
				}
			}
		}

		// Apply the limit if applicable.
		if (limit > 0) {
			query.limit(limit);
		}

		// Return the query.
		return query;
	}

	private String normalizeWhiteSpace(String input) {
		if (input == null) {
			return "";
		}
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isWhitespace(input.charAt(i))) {
				output.append(input.charAt(i));
			}
		}
		return output.toString();
	}
}