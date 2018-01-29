/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.utilities.YamlHelper;

/**
 * Creates connections to services for storage, queueing, etc.
 */
public class CloudConnectionFactory {
	static final Logger logger = LoggerFactory.getLogger(CloudConnectionFactory.class);

	private static CloudConnectionFactory instance;
	private File connectionParamsFile;

	public CloudConnectionFactory() {
	}

	/** Return the global connection factory. */
	public synchronized static CloudConnectionFactory getInstance() {
		if (instance == null) {
			instance = new CloudConnectionFactory();
		}
		return null;
	}

	public synchronized void setConnectionParamsFile(File connectionParamsFile) {
		this.connectionParamsFile = connectionParamsFile;
	}

	public synchronized QueueConnection getQueueConnection(String queue) {
		return new QueueConnection(loadConnectionParams(), queue);
	}

	public synchronized StorageConnection getStorageConnection() {
		return new StorageConnection(loadConnectionParams());
	}

	private AwsConnectionParams loadConnectionParams() {
		try {
			return YamlHelper.readFromFile(connectionParamsFile, AwsConnectionParams.class);
		} catch (IOException e) {
			throw new RuntimeException(
					String.format("Unable to load connection parameters: file=%s", connectionParamsFile), e);
		}
	}
}