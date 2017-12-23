/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import java.security.Key;
import java.sql.Date;
import java.sql.Timestamp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Implements helper methods for authorization. Token implementation is JWT.
 * This class abstracts procedures to generate, validate, and exchange tokens
 * for new ones.
 */
public class AuthTokenManager {
	/** Key used for signing tokens. */
	private final Key privateKey;
	private final SignatureAlgorithm algorithm = SignatureAlgorithm.HS512;

	public AuthTokenManager(Key privateKey) {
		this.privateKey = privateKey;
	}

	/** Returns a signed compact JWS token. */
	public String create(AuthTokenRequest request) {
		long now = System.currentTimeMillis();
		long expiration = now + (30 * 60 * 1000);
		AuthToken token = new AuthToken();
		token.setUsername(request.getUsername());
		token.setUserId(request.getUserId());
		token.setTenantId(request.getTenantId());
		token.setAdmin(request.isAdmin());
		token.setIssuedAt(new Timestamp(now));
		token.setExpiresAt(new Timestamp(expiration));
		return encode(token);
	}

	/* Encodes a token. */
	public String encode(AuthToken token) {
		String compactJws = Jwts.builder().setIssuer("goldfin.io").setSubject(token.getUsername())
				.claim("tenantId", token.getTenantId()).claim("userId", token.getUserId())
				.claim("isAdmin", token.isAdmin()).setIssuedAt(new Date(token.getIssuedAt().getTime()))
				.setExpiration(new Date(token.getExpiresAt().getTime())).signWith(algorithm, privateKey.getEncoded())
				.compact();
		return compactJws;
	}

	/**
	 * Decodes a compact JWS token.
	 * 
	 * @throws SecurityException
	 *             Thrown if signature verification fails
	 */
	public AuthToken decode(String encodedToken) throws SecurityException {
		// Parse and validate token.
		Jws<Claims> jws;
		try {
			jws = Jwts.parser().setSigningKey(privateKey).parseClaimsJws(encodedToken);
		} catch (Exception e) {
			throw new SecurityException(String.format("JWS parsing failed: %s", e.getMessage()), e);
		}
		String tokenAlgoName = jws.getHeader().getAlgorithm();
		SignatureAlgorithm tokenAlgo = SignatureAlgorithm.forName(tokenAlgoName);
		if (tokenAlgo != algorithm) {
			throw new SecurityException(String.format("Unsupported JWS signing algorithm: required=%s, actual=%s",
					algorithm.toString(), tokenAlgo.toString()));
		}

		// Generate and return auth token.
		Claims claims = jws.getBody();
		AuthToken token = new AuthToken();
		token.setTenantId((String) claims.get("tenantId"));
		token.setUserId((String) claims.get("userId"));
		token.setUsername(claims.getSubject());
		token.setAdmin((Boolean) claims.get("isAdmin"));
		token.setIssuedAt(new Timestamp(claims.getIssuedAt().getTime()));
		token.setExpiresAt(new Timestamp(claims.getExpiration().getTime()));
		return token;
	}
}