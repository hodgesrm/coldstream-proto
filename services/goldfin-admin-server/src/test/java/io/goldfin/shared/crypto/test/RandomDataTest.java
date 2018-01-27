/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto.test;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.crypto.Randomizer;
import org.junit.Assert;

/**
 * Verify generation of random bytes.
 */
public class RandomDataTest {
	static final Logger logger = LoggerFactory.getLogger(RandomDataTest.class);

	/**
	 * Verify that random byte generation creates unique values for strings up to 20
	 * bytes.
	 */
	@Test
	public void testPasswordHashingAndValidation() throws Exception {
		Randomizer random = new Randomizer();
		logger.debug("Randomizer algorithm: " + random.getAlgorithm());

		for (int i = 8; i <= 10; i++) {
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			List<String> list = new ArrayList<String>();
			for (int j = 0; j < 5; j++) {
				String randomValue = random.base64RandomBytes(i * 2);
				list.add(randomValue);
				map.put(randomValue, true);
				logger.debug(String.format("bytes: %d, string: %s", i * 2, randomValue));
			}
			Assert.assertEquals("Random string collision detected: " + list.toString(), list.size(), map.size());
		}
	}
}