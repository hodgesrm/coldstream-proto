/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity for internal API key data.
 */
public class ApiKeyData {
	private UUID id;
	private UUID userId;
	private String name;
	private String secretHash;
	private String algorithm;
	private Timestamp lastTouchedDate;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecretHash() {
		return secretHash;
	}

	public void setSecretHash(String secretHash) {
		this.secretHash = secretHash;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Timestamp getLastTouchedDate() {
		return lastTouchedDate;
	}

	public void setLastTouchedDate(Timestamp lastTouchedDate) {
		this.lastTouchedDate = lastTouchedDate;
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
		ApiKeyData apiKey = (ApiKeyData) o;
		return Objects.equals(this.id, apiKey.id) && Objects.equals(this.userId, apiKey.userId)
				&& Objects.equals(this.secretHash, apiKey.secretHash) && Objects.equals(this.algorithm, apiKey.algorithm)
				&& Objects.equals(this.lastTouchedDate, apiKey.lastTouchedDate)
		&& Objects.equals(this.creationDate, apiKey.creationDate);
	}
}
