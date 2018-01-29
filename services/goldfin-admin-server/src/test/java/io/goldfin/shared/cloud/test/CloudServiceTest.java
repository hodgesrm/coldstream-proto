/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.QueueResponse;
import io.goldfin.shared.cloud.StorageConnection;
import io.goldfin.shared.crypto.Sha256HashingAlgorithm;

/**
 * Verify operations on cloud services.
 */
public class CloudServiceTest {
	static final Logger logger = LoggerFactory.getLogger(CloudServiceTest.class);
	CloudConnectionFactory factory;

	@Before
	public void setup() {
		factory = new CloudConnectionFactory();
		factory.setConnectionParamsFile(new File("conf/aws-test.yaml"));
	}

	/**
	 * Validate that we can create and delete queues.
	 */
	@Test
	public void testQueueCreateDelete() throws Exception {
		QueueConnection conn = factory.getQueueConnection("test-queue-create-delete");

		// Delete the queue to clean up any existing queue.
		conn.queueDelete();
		boolean exists1 = conn.queueExists();
		Assert.assertFalse("Test queue does not exist", exists1);

		// Create the queue.
		conn.queueCreate(false);
		boolean exists2 = conn.queueExists();
		Assert.assertTrue("Test queue created", exists2);

		// Show that we can create over an existing queue, ignoring the error.
		conn.queueCreate(true);

		// Delete the queue again.
		conn.queueDelete();
		boolean exists3 = conn.queueExists();
		Assert.assertFalse("Test queue deleted", exists3);
	}

	/**
	 * Validate that we can insert a JSON message to the queue and read it back,
	 * then delete it, leaving the queue empty.
	 */
	@Test
	public void testQueueReadAndWrite() throws Exception {
		QueueConnection conn = factory.getQueueConnection("test-queue-read-and-write");

		// Clear existing queue and create a new one.
		conn.queueDelete();
		conn.queueCreate(false);

		// Send a message to the queue.
		SampleMessage input = new SampleMessage(1, "a string");
		conn.sendJsonMessage(input);

		// Read the message back.
		QueueResponse<SampleMessage> response = conn.receiveJsonMessage(SampleMessage.class);
		Assert.assertNotNull("Expect response", response);
		SampleMessage output = response.getObject();
		Assert.assertNotNull("Expect object", output);
		Assert.assertEquals("Expect same message in return", input, output);

		// Delete the message.
		conn.deleteMessage(response.getKey());

		// Show that further calls do not return a message.
		QueueResponse<SampleMessage> emptyResponse = conn.receiveJsonMessage(SampleMessage.class);
		Assert.assertNull("Do not expect response", emptyResponse);

		// Delete the queue.
		conn.queueDelete();
	}

	/**
	 * Validate that we can insert a document in storage, retrieve it, and delete
	 * it.
	 */
	@Test
	public void testStorageReadAndWrite() throws Exception {
		StorageConnection conn = factory.getStorageConnection();

		// Create a test document.
		String tenantId = UUID.randomUUID().toString();
		String docId = UUID.randomUUID().toString();
		File dataFile = createDataFile();
		String description = "testStorageReadAndWrite";
		String sha256 = Sha256HashingAlgorithm.bytesToHexString(Sha256HashingAlgorithm.generateHash(dataFile));
		long contentLength = dataFile.length();
		String contentType = "text/plain";

		// Store the document.
		String locator = null;
		logger.debug(String.format("Storing document: %s", dataFile.getAbsolutePath()));
		try (FileInputStream input = new FileInputStream(dataFile)) {
			locator = conn.storeTenantDocument(tenantId, docId, input, dataFile.getName(), description, sha256,
					contentLength, contentType);
		} catch (Exception e) {
			dataFile.delete();
		}
		logger.debug(String.format("Test document created: %s", locator));
		Assert.assertNotNull("Locator may not be null", locator);

		// Delete the document.
		try {
			conn.deleteTenantDocument(tenantId, docId);
		} finally {
			dataFile.delete();
		}
	}

	/** Create a test file with some content. */
	private File createDataFile() throws IOException {
		File dataFile = Files.createTempFile("testStorageReadAndWrite", ".dat", new FileAttribute<?>[0]).toFile();
		FileWriter fw = new FileWriter(dataFile);
		fw.write("test test test test test test");
		fw.close();
		return dataFile;
	}
}