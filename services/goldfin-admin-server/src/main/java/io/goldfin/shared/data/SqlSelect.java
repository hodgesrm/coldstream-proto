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
 * <li><em>project</em> adds one or more columns to the SQL projection including
 * both normal columns and window functions. If no column alias is provided the
 * column aliases to the first from object.</li>
 * <li><em>from</em> adds a 'from object', i.e., a table or subquery</li>
 * <li><em>where</em> add a where clause expression</li>
 * <li><em>window</em> adds a window expression</li>
 * <li><em>orderBy</em> adds an ordering column and direction</li>
 * </ul>
 * Like other classes in this library the run() method then gets the job done.
 */
public class SqlSelect {
	static final Logger logger = LoggerFactory.getLogger(SqlSelect.class);

	/** Specifies an ascending or descending sort. */
	public enum Ordering {
		ASC, DESC
	};

	/** Stores the definition of a column in a SQL projection. */
	class ColumnExpression {
		String tableAlias;
		String windowAlias;
		String expression;
		String alias;
	}

	/** Stores definition of a FROM-entity: a table or subquery. */
	class FromExpression {
		String alias;
		String name;
		SqlSelect subQuery;
	}

	/** Stores a window definition. */
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

	// Properties of the query.
	private List<FromExpression> froms = new ArrayList<FromExpression>();
	private List<ColumnExpression> columns = new ArrayList<ColumnExpression>();
	private List<WindowExpression> windows = new ArrayList<WindowExpression>();
	private String where = "1 = 1";
	private Object[] whereParams = {};
	private List<OrderByExpression> sorts = new ArrayList<OrderByExpression>();

	public SqlSelect() {
	}

	/** Add a from clause for a table with no alias. */
	public SqlSelect from(String tableName) {
		return from(tableName, null, null);
	}

	/** Add a from clause for a table including an alias. */
	public SqlSelect from(String tableName, String alias) {
		return from(tableName, null, alias);
	}

	/** Add a subselect, which must have an alias to be usable. */
	public SqlSelect from(SqlSelect subQuery, String alias) {
		return from(null, subQuery, alias);
	}

	private SqlSelect from(String name, SqlSelect subQuery, String alias) {
		FromExpression from = new FromExpression();
		from.name = name;
		from.subQuery = subQuery;
		from.alias = alias;
		froms.add(from);
		return this;
	}

	/** Add a from clause for a subquery. */
	public SqlSelect from(String alias, SqlSelect subQuery) {

		return this;
	}

	/** Add column names to projection. */
	public SqlSelect project(String... names) {
		for (String name : names) {
			this.project(name);
		}
		return this;
	}

	/**
	 * Add to projection a single column with name and default from/alias values.
	 */
	public SqlSelect project(String expression) {
		return this.project(null, expression, null);
	}

	/** Select a single column to projection. */
	public SqlSelect project(String tableAlias, String expression, String alias) {
		ColumnExpression column = new ColumnExpression();
		column.tableAlias = tableAlias;
		column.expression = expression;
		column.alias = alias;
		this.columns.add(column);
		return this;
	}

	/** Add a window function to projection. */
	public SqlSelect projectWindow(String windowAlias, String expression, String alias) {
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
	public SqlSelect whereId(int id) {
		return where("id = ?", id);
	}

	/** Adds standard where clause for ID-based key lookup. */
	public SqlSelect whereId(String id) {
		return where("id = ?", id);
	}

	/** Adds standard where clause for ID-based key lookup. */
	public SqlSelect whereId(UUID id) {
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
		sorts.add(expression);
		return this;
	}

	/** Construct a full query ready for parameter insertion. */
	public String build() {
		// Add clauses to build up the query. Each routine contributes
		// a well-formed clause or an empty string.
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(this.selectClause());
		queryBuf.append(this.fromClause());
		queryBuf.append(this.whereClause());
		queryBuf.append(this.windowClause());
		queryBuf.append(this.orderByClause());
		return queryBuf.toString();
	}

	/** Return the current WHERE clause parameter list. */
	public Object[] getWhereParams() {
		return whereParams;
	}

	/** Build and execute the SQL query. */
	public TabularResultSet run(Session session) throws DataException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			String query = build();
			if (logger.isDebugEnabled()) {
				logger.debug("QUERY: " + query);
			}

			// Allocate prepared statement and assign parameter values by iterating first
			// through any subqueries then the main query to ensure assignment in correct
			// order within the generated string.
			pstmt = session.getConnection().prepareStatement(query);
			List<SqlSelect> selects = new ArrayList<SqlSelect>();
			for (FromExpression from : froms) {
				if (from.subQuery != null) {
					selects.add(from.subQuery);
				}
			}
			selects.add(this);

			int paramIndex = 1;
			for (SqlSelect select : selects) {
				for (Object whereParam : select.getWhereParams()) {
					pstmt.setObject(paramIndex++, whereParam);
				}
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

	private String selectClause() {
		if (columns.size() == 0) {
			throw new DataException("Query does not have any select columns in projection");
		} else {
			StringBuffer columnList = new StringBuffer();
			columnList.append("SELECT ");
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
			return columnList.toString();
		}
	}

	/**
	 * FROM clauses are not optional in the current implementation, since leaving
	 * them out is most likely a bug.
	 */
	private String fromClause() {
		if (this.froms.size() == 0) {
			throw new DataException("Query does not have any FROM objects");
		} else {
			StringBuffer fromList = new StringBuffer();
			fromList.append(" FROM ");
			for (int i = 0; i < froms.size(); i++) {
				if (i > 0) {
					fromList.append(", ");
				}
				FromExpression from = froms.get(i);
				if (from.name != null) {
					fromList.append(from.name);
				} else if (from.subQuery != null) {
					fromList.append("( ").append(from.subQuery.build()).append(")");
				}
				if (from.alias != null) {
					fromList.append(" AS ").append(from.alias);
				}
			}
			return fromList.toString();
		}
	}

	/** WINDOW clause are optional. */
	private String windowClause() {
		if (windows.size() == 0) {
			return "";
		} else {
			StringBuffer windowList = new StringBuffer();
			windowList.append(" WINDOW ");
			for (int i = 0; i < windows.size(); i++) {
				if (i > 0) {
					windowList.append(", ");
				}
				WindowExpression window = windows.get(i);
				windowList.append(window.alias).append(" AS (PARTITION BY ");
				for (int j = 0; j < window.partitionNames.size(); j++) {
					if (j > 0) {
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
			return windowList.toString();
		}
	}

	private String whereClause() {
		return " WHERE " + this.where;
	}

	/**
	 * Generate the ORDER BY list, which is optional in the current implementation.
	 */
	private String orderByClause() {
		if (this.sorts.size() == 0) {
			return "";
		} else {
			StringBuffer orderByClause = new StringBuffer(" ORDER BY");
			for (int i = 0; i < sorts.size(); i++) {
				OrderByExpression expression = sorts.get(i);
				if (i > 0) {
					orderByClause.append(", ");
				}
				orderByClause.append(String.format(" %s %s", expression.name, expression.order.toString()));
			}
			return orderByClause.toString();
		}

	}
}