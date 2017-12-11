/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;

/**
 * Service methods for working with users.
 */
public class UserService {
	static final Logger logger = LoggerFactory.getLogger(UserService.class);

	public User create(UserParameters request) {
		return null;
	}

	public void update(String id, UserParameters request) {
	}

	public void delete(String id) {
	}

	public User get(String id) {
		return null;
	}

	public User getByUsername(String username) {
		return null;
	}

	public User[] getAll() {
		return null;
	}
}