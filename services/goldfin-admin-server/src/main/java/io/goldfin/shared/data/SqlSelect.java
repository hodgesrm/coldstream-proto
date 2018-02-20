/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Performs a SQL SELECT using a prepared statement.
 */
public class SqlSelect {
	enum Ordering {
		ASC, DESC
	};

	class OrderByExpression {
		String name;
		Ordering order;
	}

	private String table;
	private String[] names;
	private String where = "1 = 1";
	private Object[] whereParams = {};
	private List<OrderByExpression> sortExpressions = new ArrayList<OrderByExpression>();

	public SqlSelect() {
	}

	public SqlSelect table(String table) {
		this.table = table;
		return this;
	}

	public SqlSelect get(String... names) {
		this.names = names;
		return this;
	}

	public SqlSelect id(String id) {
		return where("id = ?", id);
	}

	public SqlSelect id(UUID id) {
		return where("id = ?", id);
	}

	public SqlSelect where(String where, Object... value) {
		this.where = where;
		this.whereParams = value;
		return this;
	}

	public SqlSelect orderByAscending(String name) {
		return orderBy(name, Ordering.ASC);
	}

	public SqlSelect orderByDescending(String name) {
		return orderBy(name, Ordering.DESC);
	}

	private SqlSelect orderBy(String name, Ordering order) {
		OrderByExpression expression = new OrderByExpression();
		expression.name = name;
		expression.order = order;
		sortExpressions.add(expression);
		return this;
	}

	public TabularResultSet run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			// Generate insert SQL.
			String format = "SELECT %s FROM %s WHERE %s";
			StringBuffer columnList = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				if (i > 0) {
					columnList.append(", ");
				}
				columnList.append(names[i]);
			}
			String query = String.format(format, columnList, table, where);

			// If we have order by expressions, add to the end of the query.
			if (this.sortExpressions.size() > 0) {
				StringBuffer orderByClause = new StringBuffer(" ORDER BY");
				for (int i = 0; i < sortExpressions.size(); i++) {
					OrderByExpression expression = sortExpressions.get(i);
					if (i > 0) {
						orderByClause.append(", ");
					}
					orderByClause.append(String.format(" %s %s", expression.name, expression.order.toString()));
				}
				query += orderByClause.toString();
			}

			// Allocate prepared statement and assign parameter values.
			pstmt = session.getConnection().prepareStatement(query);
			for (int i = 0; i < whereParams.length; i++) {
				pstmt.setObject(i + 1, whereParams[i]);
			}

			// Execute and return the result.
			resultSet = pstmt.executeQuery();
			return new TabularResultSet(resultSet);
		} catch (SQLException e) {
			throw new DataException(e.getLocalizedMessage(), e);
		} finally {
			JdbcUtils.closeSoftly(resultSet);
			JdbcUtils.closeSoftly(pstmt);
		}
	}
}