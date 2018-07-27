/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates connections to services for storage, queueing, etc.
 */
public class CloudConnectionFactory {
	static final Logger logger = LoggerFactory.getLogger(CloudConnectionFactory.class);

	private static CloudConnectionFactory instance;
	private AwsParams connectionParams;

	public CloudConnectionFactory() {
	}

	/** Return the global connection factory. */
	public synchronized static CloudConnectionFactory getInstance() {
		if (instance == null) {
			instance = new CloudConnectionFactory();
		}
		return instance;
	}

	/**
	 * Must be set once so that the factory knows where to find connection
	 * parameters.
	 */
	public synchronized void setConnectionParams(AwsParams connectionParams) {
		this.connectionParams = connectionParams;
	}

	/**
	 * Create a queue connection.
	 * 
	 * @param queue
	 *            A stub queue name that is unique within the service group.
	 */
	public synchronized QueueConnection getQueueConnection(String queue) {
		return new QueueConnection(connectionParams, queue);
	}

	/**
	 * Create an S3 storage connection.
	 * 
	 * @param bucket
	 *            A stub bucket name that is unique within the service group.
	 */
	public synchronized StorageConnection getStorageConnection(String bucket) {
		return new StorageConnection(connectionParams, bucket);
	}
}