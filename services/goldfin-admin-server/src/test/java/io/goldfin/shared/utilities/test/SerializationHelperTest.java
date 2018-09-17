/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities.test;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.utilities.SerializationHelper;

/**
 * Tests serialization operations.
 */
public class SerializationHelperTest {
	static final Logger logger = LoggerFactory.getLogger(SerializationHelperTest.class);

	/**
	 * Verify that we can perform round-trip serialization of data to/from gzip.
	 */
	@Test
	public void testGzipRoundTrip() throws IOException {
		String sample = "abcdefghijklmnopqrstuvwxyz!";

		ByteBuffer gzippedData = SerializationHelper.serializeToGzipBytes(sample);
		Assert.assertNotNull(gzippedData);

		String sample2 = SerializationHelper.deserializefromGzipBytes(gzippedData);
		Assert.assertNotNull(sample);
		Assert.assertEquals(sample, sample2);
	}
}