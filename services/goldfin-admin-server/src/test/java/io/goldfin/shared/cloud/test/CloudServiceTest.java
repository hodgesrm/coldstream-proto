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
import io.goldfin.shared.cloud.StorageConnection;
import io.goldfin.shared.cloud.StorageConnection.Locator;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.crypto.Sha256HashingAlgorithm;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Verify operations on cloud services.
 */
public class CloudServiceTest {
	static final Logger logger = LoggerFactory.getLogger(CloudServiceTest.class);
	CloudConnectionFactory factory;

	@Before
	public void setup() {
		factory = new CloudConnectionFactory();
		factory.setConnectionParamsFile(FileHelper.getConfigFile("aws-test.yaml"));
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
		String tenantId = UUID.randomUUID().toString();
		StructuredMessage request = new StructuredMessage().setOperation("test").setType("request")
				.setXactTag("test_xact").setTenantId(tenantId).encodeContent(input);
		conn.send(request);

		// Read the message back.
		StructuredMessage response = conn.receive(false);
		Assert.assertNotNull("Expect response", response);
		SampleMessage output = response.decodeContent(SampleMessage.class);
		Assert.assertNotNull("Expect object", output);
		Assert.assertEquals("Expect same message in return", input, output);

		Assert.assertEquals("Check operation", "test", response.getOperation());
		Assert.assertEquals("Check type", "request", response.getType());
		Assert.assertEquals("Check xact tag", "test_xact", response.getXactTag());
		Assert.assertEquals("Check tenantId", tenantId, response.getTenantId());
		Assert.assertEquals("Check contentClass", SampleMessage.class.getSimpleName(), response.getContentClass());

		// Delete the message.
		conn.deleteMessage(response);

		// Show that further calls do not return a message.
		StructuredMessage emptyResponse = conn.receive(false);
		Assert.assertNull("Do not expect response", emptyResponse);

		// Delete the queue.
		conn.queueDelete();
	}

	/**
	 * Validate that we can accept an Amazon S3 as a locator string and properly
	 * return components.
	 */
	@Test
	public void testLocator() throws Exception {
		Locator loc = new StorageConnection.Locator(
				"https://unit-test-service-goldfin-io.s3.us-west-1.amazonaws.com/tenant/b0711f8a-6b4b-4c90-bef8-5efde48f5040/7e388634-83ea-4153-bda8-e4e0656e995f");
		Assert.assertEquals("Bucket name", "unit-test-service-goldfin-io", loc.getBucket());
		Assert.assertEquals("Region", "us-west-1", loc.getRegion());
		Assert.assertEquals("Key", "tenant/b0711f8a-6b4b-4c90-bef8-5efde48f5040/7e388634-83ea-4153-bda8-e4e0656e995f",
				loc.getKey());
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
			throw e;
		}
		logger.debug(String.format("Test document created: %s", locator));
		Assert.assertNotNull("Locator may not be null", locator);

		// Fetch the document to a temporary file.
		File fetchedFile = File.createTempFile("testStorageReadBack", ".dat");
		long fetchedContentLength = conn.fetchDocument(locator, fetchedFile);
		Assert.assertEquals("Written and stored content length must be equal", contentLength, fetchedContentLength);
		String fetchedSha256 = Sha256HashingAlgorithm
				.bytesToHexString(Sha256HashingAlgorithm.generateHash(fetchedFile));
		Assert.assertEquals("Written and stored content SHA-256 must be equal", sha256, fetchedSha256);

		// Delete the document. We can discard the test files as this point.
		try {
			conn.deleteTenantDocument(tenantId, docId);
		} finally {
			fetchedFile.delete();
			dataFile.delete();
		}

		// Prove the document is gone by trying to fetch again.
		boolean failed = false;
		File deletedFile = File.createTempFile("testStorageReadBack", ".dat");
		try {
			conn.fetchDocument(locator, deletedFile);
		} catch (RuntimeException e) {
			logger.debug("Caught expected exception");
			failed = true;
		}
		if (!failed) {
			throw new Exception("Able download deleted document without error");
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