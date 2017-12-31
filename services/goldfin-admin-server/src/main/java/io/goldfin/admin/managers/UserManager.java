/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.SessionData;
import io.goldfin.admin.data.SessionDataService;
import io.goldfin.admin.data.TenantDataService;
import io.goldfin.admin.data.UserData;
import io.goldfin.admin.data.UserDataService;
import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.exceptions.NoSessionFoundException;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.admin.service.api.model.UserPasswordParameters;
import io.goldfin.admin.service.api.service.NotFoundException;
import io.goldfin.shared.crypto.BcryptHashingAlgorithm;
import io.goldfin.shared.crypto.Randomizer;
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
	private Randomizer randomizer = new Randomizer();

	/** Session timeout (defaults to 30 minutes) */
	private static long LOGIN_TIMEOUT_MILLIS = 1800 * 1000;

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

		// Check and hash password.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		String passwordHash = hashPassword(algorithm, userParams.getInitialPassword());

		// Create the user.
		UserDataService userService = new UserDataService();
		UserData model = new UserData();
		model.setUsername(userParams.getUsername());
		model.setTenantId(userParams.getTenantId());
		model.setPasswordHash(passwordHash);
		model.setAlgorithm(algorithm.getName());
		model.setRoles(userParams.getRoles());

		try (Session session = makeSession(userService)) {
			String id = userService.create(model);
			session.commit();
			return id;
		}
	}

	public void deleteUser(String id) {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession(userService)) {
			userRecord = userService.get(id);
			if (userRecord == null) {
				throw new EntityNotFoundException("User does not exist");
			}
		}

		// Delete the user. This will cascade through to sessions
		// automatically.
		logger.info("Deleting user: id=" + id + " username=" + userRecord.getUsername());
		try (Session session = context.adminSession(userService)) {
			userService.delete(id);
			session.commit();
		}
	}

	public void updateUser(String id, UserParameters userParams) throws NotFoundException {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession(userService)) {
			userRecord = userService.get(id);
			if (userRecord == null) {
				throw new EntityNotFoundException("User does not exist");
			}
		}

		// Apply parameters to the body and update.
		userRecord.setUsername(userParams.getUsername());
		userRecord.setRoles(userParams.getRoles());
		try (Session session = context.adminSession(userService)) {
			userService.update(id, userRecord);
			session.commit();
		}
	}

	public void updateUserPassword(String id, UserPasswordParameters passwordParams) throws NotFoundException {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession(userService)) {
			userRecord = userService.get(id);
			if (userRecord == null) {
				throw new EntityNotFoundException("User does not exist");
			}
		}

		// Validate the old password.
		boolean success = checkPassword(passwordParams.getOldPassword(), userRecord.getPasswordHash());
		if (!success) {
			throw new InvalidInputException("Old password does not match");
		}

		// Generate new password hash.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		String passwordHash = hashPassword(algorithm, passwordParams.getNewPassword());

		// Apply parameters to user record and update.
		userRecord.setPasswordHash(passwordHash);
		userRecord.setAlgorithm(algorithm.getName());
		try (Session session = context.adminSession(userService)) {
			userService.update(id, userRecord);
			session.commit();
		}
	}

	public User getUser(String id) {
		UserDataService userService = new UserDataService();
		try (Session session = context.adminSession(userService)) {
			UserData userData = userService.get(id);
			if (userData == null) {
				throw new EntityNotFoundException("User does not exist");
			}
			User user = toUser(userData);
			return user;
		}
	}

	public List<User> getAllUsers() {
		UserDataService userService = new UserDataService();
		try (Session session = context.adminSession(userService)) {
			List<UserData> userData = userService.getAll();
			List<User> users = new ArrayList<User>(userData.size());
			for (UserData userDatum : userData) {
				users.add(toUser(userDatum));
			}
			return users;
		}
	}

	private User toUser(UserData userData) {
		User user = new User();
		user.setId(userData.getId());
		user.setTenantId(userData.getTenantId());
		user.setUsername(userData.getUsername());
		user.setRoles(userData.getRoles());
		user.setCreationDate(userData.getCreationDate().toString());
		return user;
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
		boolean success = checkPassword(credentials.getPassword(), user.getPasswordHash());
		if (!success) {
			logger.warn(String.format("Login failed due to invalid password: user=%s", credentials.getUser()));
			return null;
		}

		// Generate a session and return the token.
		SessionDataService sessionService = new SessionDataService();
		SessionData model = new SessionData();
		model.setUserId(user.getId());
		model.setToken(randomizer.base64RandomBytes(20));
		try (Session session = makeSession(sessionService)) {
			String id = sessionService.create(model);
			logger.info(String.format("Successful login: user=%s, tenant=%s, session=%s, token=%s", user.getUsername(),
					user.getTenantId(), id, model.getToken()));
			session.commit();
			return model.getToken();
		}
	}

	public void logout(String token) {
		SessionDataService sessionService = new SessionDataService();
		SessionData sessionData = null;
		// Look up the session using the token.
		try (Session session = makeSession(sessionService)) {
			sessionData = sessionService.getByToken(token);
			if (sessionData == null) {
				logger.warn(String.format("Logout failed on unknown session: token=%s", token));
				return;
			}
		}

		// Delete the session.
		try (Session session = makeSession(sessionService)) {
			sessionService.delete(sessionData.getId().toString());
			session.commit();
			logger.info(String.format("Successful logout: userId=%s, sessionId=%s, token=%s", sessionData.getUserId(),
					sessionData.getId().toString(), token));
		}
	}

	/** Common code to check and hash password. */
	private String hashPassword(BcryptHashingAlgorithm algorithm, String password) {
		if (password == null || password.length() >= 8) {
			throw new InvalidInputException("Password must be at least 8 characters");
		} else {
			return algorithm.generateHash(password);
		}
	}

	/** Common code to check password. Ensures that we always do it the same way. */
	private boolean checkPassword(String candidate, String hash) {
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		return algorithm.validateHash(candidate, hash);
	}

	/**
	 * Validates an incoming token and if successful returns the session data. This
	 * updates the session timeout as a side effect.
	 * 
	 * @throws NoSessionFoundException
	 *             Thrown if session does not exist or has timed out
	 */
	public SessionData validate(String token) {
		// Generate a session and return the token.
		SessionDataService sessionService = new SessionDataService();
		try (Session session = makeSession(sessionService, false)) {
			// Check session and ensure it has not expired.
			SessionData sessionData = sessionService.getByToken(token);
			if (sessionData == null) {
				throw new NoSessionFoundException("Invalid session token");
			}
			long now = System.currentTimeMillis();
			long expiration = sessionData.getLastTouched().getTime() + LOGIN_TIMEOUT_MILLIS;
			if (now > expiration) {
				logger.warn(String.format("Login timeout: userId=%s", sessionData.getUserId()));
				throw new NoSessionFoundException("Session timed out");
			}

			// Update access time and return session data.
			sessionData.setLastTouched(new Timestamp(now));
			sessionService.update(sessionData.getId().toString(), sessionData);
			return sessionData;
		}
	}

	private Session makeSession(TransactionalService<?> ts) {
		return makeSession(ts, true);
	}

	private Session makeSession(TransactionalService<?> ts, boolean transactional) {
		SimpleJdbcConnectionManager cm = context.getConnectionManager();
		String schema = context.getAdminSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).addService(ts).transactional(transactional)
				.build();
	}
}