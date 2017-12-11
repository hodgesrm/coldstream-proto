/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto.test;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.crypto.BcryptHashingAlgorithm;

/**
 * Verify security package functions.
 */
public class SecurityTest {
	static final Logger logger = LoggerFactory.getLogger(SecurityTest.class);
	private String[] passwords = { "123456789", "b0bbybr0wn", "short", "Q#RSTZBQT#$314tq5!!" };

	/**
	 * Verify that password hashing correctly validates both good and bad passwords
	 * in no less than 50 milliseconds per operation.
	 */
	@Test
	public void testPasswordHashingAndValidation() throws Exception {
		BcryptHashingAlgorithm algo = new BcryptHashingAlgorithm();
		for (String pw : passwords) {
			String hash = algo.generateHash(pw);
			this.checkPw(algo, hash, pw, true, 50);
			String badPw = pw + "x";
			this.checkPw(algo, hash, badPw, false, 50);
		}
	}

	private void checkPw(BcryptHashingAlgorithm algo, String hash, String candidate, boolean good, int millis) {
		long start = System.currentTimeMillis();
		boolean success = algo.validateHash(candidate, hash);
		long end = System.currentTimeMillis();
		long duration = end - start;
		String msg = String.format("Checking password: good=%b, hash=%s, candidate=%s, millis=%d, duration=%d", good,
				hash, candidate, millis, duration);
		logger.info(msg);
		if (good) {
			Assert.assertTrue(msg, success);
		} else {
			Assert.assertFalse(msg, success);
		}
		Assert.assertTrue(msg, millis < duration);
	}
}