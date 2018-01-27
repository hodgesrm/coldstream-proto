/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implements SHA-256 hashing without salt. This is used for identifying streams
 * of bytes.
 */
public class Sha256HashingAlgorithm {
	private static String NAME = "sha256";

	public String getName() {
		return NAME;
	}

	public static byte[] generateHash(byte[] value) {
		MessageDigest digest;
		digest = getMessageDigest();
		return digest.digest(value);
	}

	public static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found", e);
		}
	}

	public static String generateHashString(String value) {
		byte[] bytes = generateHash(value.getBytes(StandardCharsets.UTF_8));
		return bytesToHexString(bytes);
	}

	public static String bytesToHexString(byte[] bytes) {
		String format = "%02x";
		StringBuffer hexAsString = new StringBuffer();
		for (byte b : bytes) {
			hexAsString.append(String.format(format, b));
		}
		return hexAsString.toString();
	}
}