/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto.test;

import java.security.Key;
import java.sql.Date;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.crypto.AuthToken;
import io.goldfin.shared.crypto.AuthTokenManager;
import io.goldfin.shared.crypto.AuthTokenRequest;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Verify password encryption.
 */
public class JwtTest {
	static final Logger logger = LoggerFactory.getLogger(JwtTest.class);
	private AuthTokenManager ata;

	@Before
	public void setup() throws Exception {
		SecretKey hmacKey = KeyGenerator.getInstance("HmacSha512").generateKey();
		ata = new AuthTokenManager(hmacKey);
	}

	/**
	 * Verify that a token request returns a token that is is decodeable and matches
	 * the request.
	 */
	@Test
	public void testJwsEncodingAndDecoding() throws Exception {
		// Create a random key.
		long now = System.currentTimeMillis();

		AuthTokenRequest tr = new AuthTokenRequest();
		tr.setUsername("rob");
		tr.setTenantId(UUID.randomUUID().toString());
		tr.setUserId(UUID.randomUUID().toString());
		tr.setAdmin(true);

		String encodedToken = ata.create(tr);
		AuthToken token = ata.decode(encodedToken);

		Assert.assertEquals(tr.getUsername(), token.getUsername());
		Assert.assertEquals(tr.getUserId(), token.getUserId());
		Assert.assertEquals(tr.getTenantId(), token.getTenantId());
		Assert.assertEquals(tr.isAdmin(), token.isAdmin());
		long issuedAt = token.getIssuedAt().getTime();
		// Have to add 1000 to iat and exp because JWT rounds to seconds.
		Assert.assertTrue(issuedAt + 1000 >= now);
		Assert.assertTrue(token.getExpiresAt().getTime() + 1000 >= now + (30 * 60 * 1000));
	}

	/**
	 * Verify that we rebuff attempts to use a different but unsupported signing
	 * algorithm.
	 */
	@Test
	public void testAnotherAlgo() throws Exception {
		SignatureAlgorithm algo = SignatureAlgorithm.HS256;
		SecretKey hmacKey = KeyGenerator.getInstance("HmacSha256").generateKey();
		attemptBadSignature(algo, hmacKey);
	}

	/**
	 * Verify that we rebuff attempts to use alg=none to weasel around signature
	 * verification.
	 */
	@Test
	public void testNoneAlgo() throws Exception {
		SignatureAlgorithm algo = SignatureAlgorithm.NONE;
		attemptBadSignature(algo, null);
	}

	/**
	 * Verify that we rebuff attempts to use a different key with supported
	 * algorithm.
	 */
	@Test
	public void testDifferentKey() throws Exception {
		SignatureAlgorithm algo = SignatureAlgorithm.HS512;
		SecretKey hmacKey = KeyGenerator.getInstance("HmacSha512").generateKey();
		attemptBadSignature(algo, hmacKey);
	}

	private void attemptBadSignature(SignatureAlgorithm algo, Key secretKey) throws Exception {
		// Encode token locally.
		String compactJws1;
		JwtBuilder builder = Jwts.builder().setIssuer("goldfin.io").setSubject("some-sub").claim("tenantId", "x")
				.claim("userId", "u").claim("isAdmin", true).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 60000));
		if (secretKey == null) {
			compactJws1 = builder.compact();
		} else {
			compactJws1 = builder.signWith(algo, secretKey.getEncoded()).compact();
		}

		// Prove that it fails decoding.
		try {
			this.ata.decode(compactJws1);
			throw new Exception("Able to decode unsupported algorithm!");
		} catch (SecurityException e) {
			logger.info("Expected exception: " + e.getMessage());
		}
	}
}