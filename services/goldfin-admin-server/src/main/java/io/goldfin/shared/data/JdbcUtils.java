/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper routines for JDBC operations.
 */
public class JdbcUtils {
	/** Close a SQL connection suppressing exceptions. */
	public static void closeSoftly(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// Ignored.
			}
		}
	}

	/** Close a SQL statement suppressing exceptions. */
	public static void closeSoftly(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// Ignored.
			}
		}
	}

	/** Close a SQL result set suppressing exceptions. */
	public static void closeSoftly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// Ignored.
			}
		}
	}
}
