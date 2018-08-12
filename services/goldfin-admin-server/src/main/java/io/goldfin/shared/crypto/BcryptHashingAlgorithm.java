/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Implemented Bcrypt-based password hashing with salt value. Implementation
 * include an algorithm name that should be stored with any password generated
 * by this class to enable upgrade in future.
 */
public class BcryptHashingAlgorithm {
	private static String NAME = "bcrypt_salt12";
	private static int SALT_ROUNDS = 12;

	public String getName() {
		return NAME;
	}

	public String generateSalt() {
		return BCrypt.gensalt(SALT_ROUNDS);
	}

	/** Generates an initial password hash without external salt value. */
	public String generateHash(String password) {
		String salt = generateSalt();
		return BCrypt.hashpw(password, salt);
	}

	/** Generates a password hash with external salt value. */
	public String generateHash(String password, String salt) {
		return BCrypt.hashpw(password, salt);
	}

	/**
	 * Validates a password candidate, returning true if the candidate matches the
	 * provided hash.
	 */
	public boolean validateHash(String candidate, String hash) {
		return BCrypt.checkpw(candidate, hash);
	}
}