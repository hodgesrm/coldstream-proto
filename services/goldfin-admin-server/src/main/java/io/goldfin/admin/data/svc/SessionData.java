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
public class SessionData {
	private UUID id;
	private UUID userId;
	private UUID tenantId;
	private UUID effectiveTenantId;
	private String roles;
	private String token;
	private Timestamp lastTouched;
	private Timestamp creationDate;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public UUID getEffectiveTenantId() {
		return effectiveTenantId;
	}

	public void setEffectiveTenantId(UUID effectiveTenantId) {
		this.effectiveTenantId = effectiveTenantId;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getLastTouched() {
		return lastTouched;
	}

	public void setLastTouched(Timestamp lastTouched) {
		this.lastTouched = lastTouched;
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
		SessionData session = (SessionData) o;
		return Objects.equals(this.id, session.id) && Objects.equals(this.userId, session.userId)
				&& Objects.equals(this.token, session.token) && Objects.equals(this.lastTouched, session.lastTouched)
				&& Objects.equals(this.creationDate, session.creationDate);
	}
}