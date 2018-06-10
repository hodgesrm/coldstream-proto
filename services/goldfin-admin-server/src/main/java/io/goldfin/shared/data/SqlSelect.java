/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs a SQL SELECT using a prepared statement. Supports subqueries and
 * windowing functions. In general:
 * <ul>
 * <li><em>get</em> adds one or more columns to the SQL projection including
 * both normal columns and window functions. If no column alias is provided the
 * column aliases to the first from object.</li>
 * <li><em>from</em> adds a 'from object', i.e., a table or subquery</li>
 * <li><em>orderBy</em> adds an ordering column and direction</li>
 * <li><em>window</em> adds a window expression
 * </ul>
 */
public class SqlSelect {
	static final Logger logger = LoggerFactory.getLogger(SqlSelect.class);

	enum Ordering {
		ASC, DESC
	};

	class ColumnExpression {
		String tableAlias;
		String windowAlias;
		String expression;
		String alias;
	}

	class TableExpression {
		String alias;
		String name;
		SqlSelect subQuery;
	}

	public class WindowExpression {
		// Retain link to parent statement.
		SqlSelect parent;
		String alias;
		List<String> partitionNames = new ArrayList<String>();
		List<OrderByExpression> orderByExpressions = new ArrayList<OrderByExpression>();

		/** Pop back to select statement. */
		public SqlSelect done() {
			return parent;
		}

		/** Add a window partition value. */
		public WindowExpression partition(String name) {
			partitionNames.add(name);
			return this;
		}

		/** Add window order by value. */
		public WindowExpression orderByAscending(String name) {
			return orderBy(name, Ordering.ASC);
		}

		/** Add window order by value. */
		public WindowExpression orderByDescending(String name) {
			return orderBy(name, Ordering.DESC);
		}

		private WindowExpression orderBy(String name, Ordering order) {
			OrderByExpression expression = new OrderByExpression();
			expression.name = name;
			expression.order = order;
			orderByExpressions.add(expression);
			return this;
		}
	}

	class OrderByExpression {
		String name;
		Ordering order;
	}

	private String table;
	private List<ColumnExpression> columns = new ArrayList<ColumnExpression>();
	private List<WindowExpression> windows = new ArrayList<WindowExpression>();
	private String where = "1 = 1";
	private Object[] whereParams = {};
	private List<OrderByExpression> sortExpressions = new ArrayList<OrderByExpression>();

	public SqlSelect() {
	}

	/** Add a from clause for a table with no alias. */
	public SqlSelect from(String table) {
		this.table = table;
		return this;
	}

	/** Add given names to project. */
	public SqlSelect get(String... names) {
		for (String name : names) {
			this.get(name);
		}
		return this;
	}

	/**
	 * Add to projection a single column with name and default from/alias values.
	 */
	public SqlSelect get(String expression) {
		return this.get(null, expression, null);
	}

	/** Select a single column to projection. */
	public SqlSelect get(String tableAlias, String expression, String alias) {
		ColumnExpression column = new ColumnExpression();
		column.tableAlias = tableAlias;
		column.expression = expression;
		column.alias = alias;
		this.columns.add(column);
		return this;
	}

	/** Add a window function to projection. */
	public SqlSelect getWindow(String windowAlias, String expression, String alias) {
		ColumnExpression column = new ColumnExpression();
		column.windowAlias = windowAlias;
		column.expression = expression;
		column.alias = alias;
		this.columns.add(column);
		return this;
	}

	/**
	 * Add a window clause. Clients should issue done() call if using fluent
	 * invocation.
	 */
	public WindowExpression window(String alias) {
		WindowExpression window = new WindowExpression();
		window.alias = alias;
		window.parent = this;
		this.windows.add(window);
		return window;
	}

	/** Adds standard where clause for ID-based key lookup. */
	public SqlSelect where_id(int id) {
		return where("id = ?", id);
	}

	/** Adds standard where clause for ID-based key lookup. */
	public SqlSelect where_id(String id) {
		return where("id = ?", id);
	}

	/** Adds standard where clause for ID-based key lookup. */
	public SqlSelect where_id(UUID id) {
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
			String format = "SELECT %s FROM %s WHERE %s%s";
			StringBuffer columnList = new StringBuffer();
			for (int i = 0; i < columns.size(); i++) {
				ColumnExpression column = columns.get(i);
				if (i > 0) {
					columnList.append(", ");
				}
				if (column.tableAlias != null) {
					columnList.append(column.tableAlias).append(".");
				}
				columnList.append(column.expression);
				if (column.windowAlias != null) {
					columnList.append(" OVER ").append(column.windowAlias);
				}
				if (column.alias != null) {
					columnList.append(" AS ").append(column.alias);
				}
			}
			StringBuffer windowList = new StringBuffer();
			for (int i = 0; i < windows.size(); i++) {
				if (i > 0) {
					windowList.append(", ");
				}
				WindowExpression window = windows.get(i);
				windowList.append(" WINDOW ").append(window.alias).append(" AS ");
				windowList.append("(PARTITION BY ");
				for (int j = 0; j < window.partitionNames.size(); j++) {
					if (i > 0) {
						windowList.append(", ");
					}
					windowList.append(window.partitionNames.get(j));
				}
				if (window.orderByExpressions.size() > 0) {
					windowList.append(" ORDER BY ");
					for (int l = 0; l < window.orderByExpressions.size(); l++) {
						OrderByExpression orderBy = window.orderByExpressions.get(l);
						if (l > 0) {
							windowList.append(", ");
						}
						windowList.append(String.format(" %s %s", orderBy.name, orderBy.order.toString()));
					}
				}
				windowList.append(")");
			}
			String query = String.format(format, columnList, table, where, windowList);

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
			if (logger.isDebugEnabled()) {
				logger.debug("QUERY: " + query);
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