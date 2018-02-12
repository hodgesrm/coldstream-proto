/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implements SHA-256 hashing without salt. This is used for identifying streams
 * of bytes. Note: this class could easily be generalized to a nice wrapper
 * around MessageDigest. Maybe in a future release...
 */
public class Sha256HashingAlgorithm {
	private static String NAME = "sha256";

	public String getName() {
		return NAME;
	}

	/** Return a MessageDigest that implements SHA-256. */
	public static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found", e);
		}
	}

	/** Compute a hash digest from a byte array. */
	public static byte[] generateHash(byte[] value) {
		MessageDigest digest;
		digest = getMessageDigest();
		return digest.digest(value);
	}

	/** Compute a hash string from a byte array. */
	public static String generateHashString(byte[] value) {
		byte[] digest = generateHash(value);
		return bytesToHexString(digest);
	}

	/** Compute a hash digest from a stream. */
	public static byte[] generateHash(InputStream input) {
		MessageDigest digest = getMessageDigest();
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = input.read(buf)) >= 0) {
				digest.update(buf, 0, len);
			}
			return digest.digest();
		} catch (IOException e) {
			throw new RuntimeException("Unable to compute digest on input stream", e);
		}
	}

	/** Compute a hash string from a byte array. */
	public static String generateHashString(InputStream input) {
		byte[] digest = generateHash(input);
		return bytesToHexString(digest);
	}

	/** Compute a hash digest on a file. */
	public static byte[] generateHash(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return generateHash(fis);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to compute digest on file: %s", file.getAbsolutePath()),
					e);
		}
	}

	/** Compute a hash string on a file. */
	public static String generateHashString(File file) {
		byte[] digest = generateHash(file);
		return bytesToHexString(digest);
	}

	/** Convert bytes to hex stream representation. */
	public static String bytesToHexString(byte[] bytes) {
		String format = "%02x";
		StringBuffer hexAsString = new StringBuffer();
		for (byte b : bytes) {
			hexAsString.append(String.format(format, b));
		}
		return hexAsString.toString();
	}

	/** Helper method to compute a hex string hash on a string value. */
	public static String generateHashString(String value) {
		byte[] bytes = generateHash(value.getBytes(StandardCharsets.UTF_8));
		return bytesToHexString(bytes);
	}
}