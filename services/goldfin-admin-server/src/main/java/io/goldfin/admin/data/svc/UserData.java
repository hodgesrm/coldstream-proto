/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.svc;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity for internal user data.
 */
public class UserData {
	private UUID id;
	private UUID tenantId;
	private String username;
	private String roles;
	private String passwordHash;
	private String algorithm;
	private Timestamp creationDate;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserData user = (UserData) o;
		return Objects.equals(this.id, user.id) && Objects.equals(this.tenantId, user.tenantId)
				&& Objects.equals(this.username, user.username) && Objects.equals(this.passwordHash, user.passwordHash)
				&& Objects.equals(this.algorithm, user.algorithm) && Objects.equals(this.roles, user.roles)
				&& Objects.equals(this.creationDate, user.creationDate);
	}
}
