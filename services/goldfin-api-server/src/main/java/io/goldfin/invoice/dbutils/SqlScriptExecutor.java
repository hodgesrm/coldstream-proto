/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.dbutils;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads SQL script to DBMS via JDBC. Includes ability to parameterize scripts.
 */
public class SqlScriptExecutor {
	static final Logger logger = LoggerFactory.getLogger(SqlScriptExecutor.class);

	private final ConnectionParams connectionParams;
	private final Properties scriptProperties;

	public SqlScriptExecutor(ConnectionParams connectionParams, Properties scriptProperties) {
		this.connectionParams = connectionParams;
		this.scriptProperties = scriptProperties;
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
		DB db = null;
		SqlBatch current = null;

		try {
			// Convert the file to batches.
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Loading SQL script: script=%s, properties=%s", script.getAbsolutePath(),
						scriptProperties.toString()));
			}
			SqlScript sqlScript = new SqlScript(script, scriptProperties);
			List<SqlBatch> batches = sqlScript.getBatches();

			// Connect.
			if (logger.isDebugEnabled()) {
				logger.debug("Connecting to DBMS: driver=%s, url=%s, user=%s");
			}
			db = new DB();
			db.open(connectionParams.getDriver(), connectionParams.getUrl(), connectionParams.getUser(),
					connectionParams.getPassword());

			// Load batches.
			// db.openTransaction();
			for (int batchNumber = 0; batchNumber < batches.size(); batchNumber++) {
				current = batches.get(batchNumber);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Executing batch: lineno=%d, sql=%s", current.getLineno(),
							current.getContent()));
				}
				db.exec(current.getContent());
			}
			//db.commitTransaction();
		} catch (Exception e) {
			String msg;
			if (current != null) {
				msg = String.format("Batch load failed: msg=%s, source=%s, lineno=%d", e.getMessage(),
						current.getSource().getName(), current.getLineno());
			} else {
				msg = String.format("Batch load failed: msg=%s", e.getMessage());
			}
			logger.error(msg, e);
			//rollback(db);
			throw new SqlLoadException(msg, e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void rollback(DB db) {
		try {
			db.rollbackTransaction();
		} catch (Exception e) {
			logger.error("Unable to roll back transaction", e);
		}
	}
}