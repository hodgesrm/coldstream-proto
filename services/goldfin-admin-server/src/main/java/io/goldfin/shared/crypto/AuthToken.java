/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.crypto;

import java.sql.Timestamp;

/**
 * A validated authorization token. 
 */
public class AuthToken {
	private String issuer;
	private String tenantId;
	private String userId;
	private String username;
	private boolean isAdmin;
	private Timestamp issuedAt;
	private Timestamp expiresAt;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Timestamp getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Timestamp issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Timestamp getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}
}