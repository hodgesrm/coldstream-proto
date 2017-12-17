/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.UUID;

import io.goldfin.admin.data.UserData;
import io.goldfin.admin.data.UserDataService;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.shared.crypto.BcryptHashingAlgorithm;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;

/**
 * Handles operations related to user logins.
 */
public class LoginManager implements Manager {
	private ManagementContext context;

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	@Override
	public void prepare() {
		// Do nothing for now.
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	public String login(LoginCredentials credentials) {
		UserDataService userService = new UserDataService();
		UserData user = null;
		// Look up the user.
		try (Session session = makeSession(userService)) {
			user = userService.getByUsername(credentials.getUser());
			if (user == null) {
				return null;
			}
		}

		// Check the password by hashing with bcrypt and comparing against
		// actual hash.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		String credentialHash = algorithm.generateHash(credentials.getPassword());
		boolean success = algorithm.validateHash(credentialHash, user.getPasswordHash());
		if (!success) {
			return null;
		}

		// Generate a token.
		return UUID.randomUUID().toString();
	}

	private Session makeSession(TransactionalService<?> ts) {
		SimpleJdbcConnectionManager cm = context.getConnectionManager();
		String schema = context.getAdminSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).addService(ts).build();
	}

}