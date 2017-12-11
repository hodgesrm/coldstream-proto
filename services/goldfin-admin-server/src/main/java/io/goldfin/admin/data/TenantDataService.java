/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.TenantParameters;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;

/**
 * Service methods for working with tenant data. 
 */
public class TenantDataService {
	static final Logger logger = LoggerFactory.getLogger(TenantDataService.class);
	private SimpleJdbcConnectionManager connectionManager;

	public Tenant create(TenantParameters params) {
		return null;
	}

	public void update(String id, TenantParameters request) {
	}

	public void delete(String id) {
	}

	public Tenant get(String id) {
		return null;
	}

	public Tenant getByName(String name) {
		return null;
	}

	public Tenant[] getAll() {
		return null;
	}
}