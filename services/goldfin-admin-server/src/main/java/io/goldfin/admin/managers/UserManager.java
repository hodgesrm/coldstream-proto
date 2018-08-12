/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.ApiKeyData;
import io.goldfin.admin.data.ApiKeyService;
import io.goldfin.admin.data.SessionData;
import io.goldfin.admin.data.SessionDataService;
import io.goldfin.admin.data.TenantDataService;
import io.goldfin.admin.data.UserData;
import io.goldfin.admin.data.UserDataService;
import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.exceptions.UnauthorizedException;
import io.goldfin.admin.service.api.model.ApiKey;
import io.goldfin.admin.service.api.model.ApiKeyParameters;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.model.Tenant;
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
	private Map<String, String> apiKeyTable = new HashMap<String, String>();

	private static Pattern userAtTenantPattern = Pattern.compile("([a-zA-z0-9.]+)@([a-zA-z0-9.]+)");

	/** A special ID that represents the currently logged in user. */
	public static String CURRENT_USER_ID = "00000000-0000-0000-0000-000000000000";

	/** Salt value for API keys. This value needs to stay constant. */
	public static String SALT = "$2a$12$0NjdsCnknk9fnYcuybk.IO";

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

	public User createUser(UserParameters userParams) {
		// Get the user name and tenant name.
		String[] userTenant = this.parseLogin(userParams.getUser());
		if (userTenant == null) {
			logger.warn(String.format("Invalid user name format: user=%s", userParams.getUser()));
			throw new InvalidInputException("User name must be 'user@tenant'");
		}
		String userName = userTenant[0];
		String tenantName = userTenant[1];

		// Find the tenant.
		Tenant tenant = null;
		TenantDataService tenantService = new TenantDataService();
		try (Session session = makeSession(tenantService)) {
			tenant = tenantService.getByName(tenantName);
			if (tenant == null) {
				throw new InvalidInputException(String.format("Unknown tenant: name=%s"));
			}
		}

		// Hash password.
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		String passwordHash = hashPassword(algorithm, userParams.getInitialPassword(), null);

		// Create the user.
		UserDataService userService = new UserDataService();
		UserData model = new UserData();
		model.setUsername(userName);
		model.setTenantId(tenant.getId());
		model.setPasswordHash(passwordHash);
		model.setAlgorithm(algorithm.getName());
		model.setRoles(userParams.getRoles());

		try (Session session = makeSession(userService)) {
			String id = userService.create(model);
			session.commit();
			return this.getUser(id);
		}
	}

	public void deleteUser(String id) {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession().enlist(userService)) {
			userRecord = userService.get(id);
			if (userRecord == null) {
				throw new EntityNotFoundException("User does not exist");
			}
		}

		// Delete the user. This will cascade through to sessions
		// automatically.
		logger.info("Deleting user: id=" + id + " username=" + userRecord.getUsername());
		try (Session session = context.adminSession().enlist(userService)) {
			userService.delete(id);
			session.commit();
		}
	}

	public void updateUser(String id, UserParameters userParams) throws NotFoundException {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession().enlist(userService)) {
			userRecord = userService.get(id);
			if (userRecord == null) {
				throw new EntityNotFoundException("User does not exist");
			}
		}

		// Apply parameters to the body and update. NOTE: This is incorrect;
		// we need to revisit changing user names.
		userRecord.setUsername(userParams.getUser());
		userRecord.setRoles(userParams.getRoles());
		try (Session session = context.adminSession().enlist(userService)) {
			userService.update(id, userRecord);
			session.commit();
		}
	}

	public void updateUserPassword(String id, UserPasswordParameters passwordParams) throws NotFoundException {
		UserDataService userService = new UserDataService();

		// See if user exists.
		UserData userRecord;
		try (Session session = context.adminSession().enlist(userService)) {
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
		String passwordHash = hashPassword(algorithm, passwordParams.getNewPassword(), null);

		// Apply parameters to user record and update.
		userRecord.setPasswordHash(passwordHash);
		userRecord.setAlgorithm(algorithm.getName());
		try (Session session = context.adminSession().enlist(userService)) {
			userService.update(id, userRecord);
			session.commit();
		}
	}

	public User getUser(String id) {
		UserDataService userService = new UserDataService();
		try (Session session = context.adminSession().enlist(userService)) {
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
		try (Session session = context.adminSession().enlist(userService)) {
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

	public ApiKey createApiKey(String userId, ApiKeyParameters apiKeyParams) {
		// Ensure that the user ID actually exists and belongs to tenant.
		User user = this.getUser(userId);
		logger.info(String.format("Creating new API key: tenant=%s, userId=%s, userName=%s, apiKeyName=%s",
				user.getTenantId(), userId, user.getUsername(), apiKeyParams.getName()));

		// Ensure that the API key name is not empty.
		String name = apiKeyParams.getName().trim();
		if (name == null || "".equals(name)) {
			throw new InvalidInputException("API key name may not be empty");
		}

		// See if the key name already exists for this user.
		for (ApiKey apiKey : this.getAllApiKeys(userId)) {
			if (apiKey.getName().equals(name)) {
				throw new InvalidInputException(String.format("API key name already exists: %s", name));
			}
		}

		// Create a random secret password, and test that it will be unique. We
		// allow up to three tries though the chances that values will clash
		// should be vanishingly small.
		ApiKeyService apiKeyService = new ApiKeyService();
		String secret = null;
		String secretHash = null;
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		for (int i = 0; i < 3; i++) {
			secret = randomizer.base64RandomBytes(20);
			secretHash = hashPassword(algorithm, secret, SALT);

			try (Session session = makeSession(apiKeyService)) {
				ApiKeyData apiKeyData = apiKeyService.getBySecretHash(secretHash);
				session.commit();
				if (apiKeyData == null) {
					break;
				} else {
					logger.warn(String.format(
							"API key generation collision: userId=%s, userName=%s, apiKeyName=%s, hash=%s)",
							user.getId(), user.getUsername(), name, secretHash));
				}
			}
		}

		// At this point we're good to go. Create the API key.
		ApiKeyData model = new ApiKeyData();
		model.setUserId(UUID.fromString(userId));
		model.setName(name);
		model.setSecretHash(secretHash);
		model.setAlgorithm(algorithm.getName());
		model.setLastTouchedDate(new Timestamp(System.currentTimeMillis()));

		try (Session session = makeSession(apiKeyService)) {
			apiKeyService.create(model);
			session.commit();
		}

		// Fetch the ApiKey back and insert the secret. This is the only time we
		// expose the secret value.
		ApiKey apiKey = getApiKeyBySecretHash(secretHash);
		apiKey.setSecret(secret);
		return apiKey;
	}

	public List<ApiKey> getAllApiKeys(String userId) {
		ApiKeyService apiKeyService = new ApiKeyService();
		List<ApiKey> apiKeys = new ArrayList<ApiKey>();
		try (Session session = context.adminSession().enlist(apiKeyService)) {
			List<ApiKeyData> rawKeys = apiKeyService.getAllForUser(userId);
			for (ApiKeyData rawKey : rawKeys) {
				apiKeys.add(toApiKey(rawKey));
			}
		}
		return apiKeys;
	}

	public void deleteApiKey(String userId, String id) {
		ApiKeyService apiKeyService = new ApiKeyService();
		ApiKeyData apiKey;
		try (Session session = context.adminSession().enlist(apiKeyService)) {
			apiKey = apiKeyService.get(id);
			if (apiKey == null) {
				throw new EntityNotFoundException("API key does not exist");
			}
		}
		logger.info(String.format("Removing API key: userId=%s, id=%s, name=%s", userId, id, apiKey.getName()));
		try (Session session = context.adminSession().enlist(apiKeyService)) {
			apiKeyService.delete(id);
			session.commit();
		}
	}

	private ApiKey getApiKeyBySecretHash(String secretHash) {
		ApiKeyService apiKeyService = new ApiKeyService();
		try (Session session = makeSession(apiKeyService)) {
			ApiKeyData apiKeyData = apiKeyService.getBySecretHash(secretHash);
			session.commit();
			return toApiKey(apiKeyData);
		}
	}

	private ApiKey toApiKey(ApiKeyData apiKeyData) {
		ApiKey apiKey = new ApiKey();
		apiKey.setId(apiKeyData.getId());
		apiKey.setUserId(apiKeyData.getUserId());
		apiKey.setName(apiKeyData.getName());
		apiKey.setSecret("********");
		apiKey.setLastTouchedDate(apiKeyData.getLastTouchedDate());
		return apiKey;
	}

	public String login(LoginCredentials credentials) {
		UserDataService userService = new UserDataService();
		TenantDataService tenantService = new TenantDataService();
		UserData user = null;
		// Look up the user.
		try (Session session = makeSession(userService).enlist(tenantService)) {
			String[] userTenant = this.parseLogin(credentials.getUser());
			if (userTenant == null) {
				logger.warn(
						String.format("Login failed due to invalid user name format: user=%s", credentials.getUser()));
				throw new UnauthorizedException();
			}
			String userName = userTenant[0];
			String tenantName = userTenant[1];

			Tenant tenant = tenantService.getByName(tenantName);
			if (tenant == null) {
				logger.warn(String.format("Login failed due to unknown tenant name: user=%s, tenantName=%s",
						credentials.getUser(), tenantName));
				throw new UnauthorizedException();
			}

			user = userService.getByUserNameAndTenant(userName, tenant.getId());
			if (user == null) {
				logger.warn(String.format("Login failed due to unknown user: user=%s", credentials.getUser()));
				throw new UnauthorizedException();
			}
		}

		// Check the password by hashing with bcrypt and comparing against
		// actual hash.
		boolean success = checkPassword(credentials.getPassword(), user.getPasswordHash());
		if (!success) {
			logger.warn(String.format("Login failed due to invalid password: user=%s", credentials.getUser()));
			throw new UnauthorizedException();
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

	/**
	 * Common code to check and hash password. Salt is optional: for user passwords
	 * we let bcrypt generate salt automatically. API keys require an external salt
	 * value as we recompute the hash when verifying, then look it up, hence do not
	 * know the hash name.
	 */
	private String hashPassword(BcryptHashingAlgorithm algorithm, String password, String salt) {
		if (password == null || password.length() < 8) {
			throw new InvalidInputException("Password must be at least 8 characters");
		} else {
			if (salt == null) {
				return algorithm.generateHash(password);
			} else {
				return algorithm.generateHash(password, salt);
			}
		}
	}

	/** Common code to check password. Ensures that we always do it the same way. */
	private boolean checkPassword(String candidate, String hash) {
		BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
		return algorithm.validateHash(candidate, hash);
	}

	/**
	 * Validates an incoming session token and if successful returns the session
	 * data. This updates the session timeout as a side effect.
	 * 
	 * @throws UnauthorizedException
	 *             Thrown if session does not exist or has timed out
	 */
	public SessionData validateSessionKey(String token) {
		// Generate a session and return the token.
		SessionDataService sessionService = new SessionDataService();
		try (Session session = makeSession(sessionService, false)) {
			// Check session and ensure it has not expired.
			SessionData sessionData = sessionService.getByToken(token);
			if (sessionData == null) {
				throw new UnauthorizedException("Invalid session token");
			}
			long now = System.currentTimeMillis();
			long expiration = sessionData.getLastTouched().getTime() + LOGIN_TIMEOUT_MILLIS;
			if (now > expiration) {
				logger.warn(String.format("Login timeout: userId=%s", sessionData.getUserId()));
				throw new UnauthorizedException("Session timed out");
			}

			// Update access time and return session data.
			sessionData.setLastTouched(new Timestamp(now));
			sessionService.update(sessionData.getId().toString(), sessionData);
			return sessionData;
		}
	}

	/**
	 * Validates an API key and if successful returns the user data. This updates
	 * the API Key last touched date as a side effect.
	 * 
	 * @throws UnauthorizedException
	 *             Thrown if API token does not exist
	 */
	public User validateApiKeySecret(String apiKeySecret) {
		ApiKeyService apiKeyService = new ApiKeyService();
		try (Session session = makeSession(apiKeyService, false)) {
			// API keys are hashed so that evildoers cannot reverse-engineer
			// keys by reading the DBMS. Hashing is by design slow so we
			// use a cache to avoid hashing the secret more than once if
			// the key is valid. Invalid keys are hashed anew each time.
			String secretHash = this.apiKeyTable.get(apiKeySecret);
			boolean newHash = false;
			if (secretHash == null) {
				BcryptHashingAlgorithm algorithm = new BcryptHashingAlgorithm();
				secretHash = hashPassword(algorithm, apiKeySecret, SALT);
				newHash = true;
			}
			ApiKeyData apiKeyData = apiKeyService.getBySecretHash(secretHash);
			if (apiKeyData == null) {
				logger.warn("API key validation failed");
				throw new UnauthorizedException();
			}
			long now = System.currentTimeMillis();
			apiKeyData.setLastTouchedDate(new Timestamp(now));
			apiKeyService.update(apiKeyData.getId().toString(), apiKeyData);

			User user = this.getUser(apiKeyData.getUserId().toString());
			if (newHash) {
				this.apiKeyTable.put(apiKeySecret, secretHash);
				logger.info(String.format("Inserted hash for API Key: userId=%s, userName=%s, apiKeyName=%s", 
						user.getId(), user.getUsername(), apiKeyData.getName()));
			}
			return user;
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

	/** Split up user and tenant names. */
	private String[] parseLogin(String login) {
		Matcher loginMatcher = userAtTenantPattern.matcher(login);
		if (!loginMatcher.matches()) {
			return null;
		}
		String[] userTenant = new String[2];
		userTenant[0] = loginMatcher.group(1);
		userTenant[1] = loginMatcher.group(2);
		return userTenant;
	}
}