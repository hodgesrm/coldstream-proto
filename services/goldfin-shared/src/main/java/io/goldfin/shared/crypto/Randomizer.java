/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implements generation of cryptographically secure random tokens. This class
 * is currently a wrapper on SecureRandom.
 */
public class Randomizer {
	private SecureRandom secureRandom = new SecureRandom();
	private Base64.Encoder encoder = Base64.getEncoder();

	/** Return generation algorithm. */
	public String getAlgorithm() {
		return secureRandom.getAlgorithm();
	}

	/**
	 * Generate random bytes and return as Base64-encoded string.
	 */
	public String base64RandomBytes(int size) {
		byte[] randomBytes = randomBytes(size);
		return encoder.encodeToString(randomBytes);
	}

	/**
	 * Generate and return random bytes.
	 * 
	 * @param size
	 *            number of bytes to generate
	 */
	public byte[] randomBytes(int size) {
		byte bytes[] = new byte[size];
		secureRandom.nextBytes(bytes);
		return bytes;
	}
}
