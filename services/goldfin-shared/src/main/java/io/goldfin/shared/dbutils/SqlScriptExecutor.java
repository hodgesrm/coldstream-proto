/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.dbutils;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.DataException;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.SqlStatement;

/**
 * Loads SQL script to DBMS via JDBC. Includes ability to parameterize scripts.
 */
public class SqlScriptExecutor {
	static final Logger logger = LoggerFactory.getLogger(SqlScriptExecutor.class);

	private final Properties scriptProperties;
	private final SimpleJdbcConnectionManager connectionManager;
	private final String schema;

	/**
	 * Instantiate a new executor for SQL
	 * 
	 * @param connectionParams
	 *            Connection parameters that we can feed to connection pool
	 * @param scriptProperties
	 *            Properties to substitute into script
	 * @param schema
	 *            DBMS schema to use for script
	 */
	public SqlScriptExecutor(ConnectionParams connectionParams, Properties scriptProperties, String schema) {
		this.scriptProperties = scriptProperties;
		this.connectionManager = new SimpleJdbcConnectionManager(connectionParams);
		this.schema = schema;
	}

	/**
	 * Loads a SQL script.
	 * 
	 * @param script
	 *            File containing the script
	 * @throws SqlLoadException
	 *             Thrown if load fails.
	 */
	public void execute(File script) throws SqlLoadException {
		Session session = null;
		SqlBatch current = null;
		boolean transactional = false;

		try {
			// Convert the file to batches.
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Loading SQL script: script=%s, properties=%s", script.getAbsolutePath(),
						scriptProperties.toString()));
			}
			SqlScript sqlScript = new SqlScript(script, scriptProperties);
			List<SqlBatch> batches = sqlScript.getBatches();
			transactional = sqlScript.isTransactional();

			// Connect.
			if (schema == null) {
				session = new SessionBuilder().connectionManager(connectionManager).transactional(transactional)
						.build();
			} else {
				session = new SessionBuilder().connectionManager(connectionManager).transactional(transactional)
						.ensureSchema(schema).build();
			}

			// Load batches.
			for (int batchNumber = 0; batchNumber < batches.size(); batchNumber++) {
				current = batches.get(batchNumber);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Executing batch: lineno=%d, sql=%s", current.getLineno(),
							current.getContent()));
				}
				new SqlStatement(current.getContent()).run(session);
			}
			if (transactional) {
				session.commit();
			}
		} catch (Exception e) {
			String msg;
			if (current != null) {
				msg = String.format("Batch load failed: msg=%s, source=%s, lineno=%d", e.getMessage(),
						current.getSource().getName(), current.getLineno());
			} else {
				msg = String.format("Batch load failed: msg=%s", e.getMessage());
			}
			logger.error(msg, e);
			if (session != null && transactional) {
				try {
					session.rollback();
				} catch (DataException e2) {
					logger.warn("Unable to roll back failed transaction", e2);
				}
			}
			throw new SqlLoadException(msg, e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
}