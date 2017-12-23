/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.SessionData;
import io.goldfin.admin.data.SessionDataService;
import io.goldfin.admin.data.TenantDataService;
import io.goldfin.admin.data.UserData;
import io.goldfin.admin.data.UserDataService;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.shared.crypto.BcryptHashingAlgorithm;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;

/**
 * Handles operations related to users.
 */
public class UserManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(UserManager.class);
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

	public String createUser(UserParameters userParams) {
		// Ensure that either the user is an administrator or the user exists.
		if ("ADMIN".equalsIgnoreCase(userParams.getRoles())) {
			if (userParams.getTenantId() != null) {
				throw new InvalidInputException("Tenant ID must be null for admin user");
			}
		} else {
			TenantDataService tenantService = new TenantDataService();
			try (Session session = makeSession(tenantService)) {
				if (userParams.getTenantId() == null) {
					throw new InvalidInputException("Tenant ID may not be null");
				} else if (tenantService.get(userParams.getTenantId().toString()) == null) {
					throw new InvalidInputException("Tenant ID does not exist");
				}
			}
		}

		// Ensure initial password is at least 8 characters.
		if (userParams.getInitialPassword() == null || userParams.getInitialPassword().length() >= 8) {
			throw new InvalidInputException("Initial password must be at least 8 characters");
		}

		// Create the user.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		UserDataService userService = new UserDataService();
		UserData model = new UserData();
		model.setUsername(userParams.getUsername());
		model.setTenantId(userParams.getTenantId());
		model.setPasswordHash(algorithm.generateHash(userParams.getInitialPassword()));
		model.setAlgorithm(algorithm.getName());
		model.setRoles(userParams.getRoles());

		try (Session session = makeSession(userService)) {
			String id = userService.create(model);
			session.commit();
			return id;
		}
	}

	public String login(LoginCredentials credentials) {
		UserDataService userService = new UserDataService();
		UserData user = null;
		// Look up the user.
		try (Session session = makeSession(userService)) {
			user = userService.getByUsername(credentials.getUser());
			if (user == null) {
				logger.warn(String.format("Login failed due to unknown user: user=%s", credentials.getUser()));
				return null;
			}
		}

		// Check the password by hashing with bcrypt and comparing against
		// actual hash.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		//String credentialHash = algorithm.generateHash(credentials.getPassword());
		boolean success = algorithm.validateHash(credentials.getPassword(), user.getPasswordHash());
		if (!success) {
			logger.warn(String.format("Login failed due to invalid password: user=%s", credentials.getUser()));
			return null;
		}

		// Generate a session and return the token.
		SessionDataService sessionService = new SessionDataService();
		SessionData model = new SessionData();
		model.setUserId(user.getId());
		model.setToken(UUID.randomUUID().toString());
		try (Session session = makeSession(sessionService)) {
			String id = sessionService.create(model);
			logger.info(String.format("Successful login: user=%s, tenant=%s, session=%s", user.getUsername(),
					user.getTenantId(), id));
			session.commit();
			return model.getToken();
		}
	}

	private Session makeSession(TransactionalService<?> ts) {
		SimpleJdbcConnectionManager cm = context.getConnectionManager();
		String schema = context.getAdminSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).addService(ts).build();
	}
}