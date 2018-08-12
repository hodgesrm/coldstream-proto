//  ========================================================================
//  Copyright (c) 2017 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.admin.restapi.jetty;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.SessionData;
import io.goldfin.admin.exceptions.UnauthorizedException;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;

/**
 * Checks requests to ensure they are authorized.
 */
public class SecurityAuthenticator implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(SecurityAuthenticator.class);
	public static final String SESSION_KEY_HEADER = "vnd.io.goldfin.session";
	public static final String API_KEY_HEADER = "vnd.io.goldfin.apikey";

	@Override
	public String getAuthMethod() {
		if (logger.isDebugEnabled()) {
			logger.debug("Returning auth method: " + this.getClass().getSimpleName());
		}
		return Constraint.ANY_AUTH;
	}

	@Override
	public void prepareRequest(ServletRequest arg0) {
		if (logger.isDebugEnabled()) {
			logger.debug("Preparing request: " + this.getClass().getSimpleName());
		}
	}

	@Override
	public boolean secureResponse(ServletRequest arg0, ServletResponse arg1, boolean arg2, User arg3)
			throws ServerAuthException {
		if (logger.isDebugEnabled()) {
			logger.debug("Checking for secure response: " + this.getClass().getSimpleName());
		}
		return false;
	}

	@Override
	public void setConfiguration(AuthConfiguration arg0) {
		if (logger.isDebugEnabled()) {
			logger.debug("Setting auth configuration: " + this.getClass().getSimpleName());
		}
	}

	@Override
	public Authentication validateRequest(ServletRequest request, ServletResponse response, boolean mandatory)
			throws ServerAuthException {
		if (logger.isDebugEnabled()) {
			logger.debug("Validating request: " + this.getClass().getSimpleName());
		}

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Fetch auth headers.
		String sessionKey = req.getHeader(SESSION_KEY_HEADER);
		String apiKey = req.getHeader(API_KEY_HEADER);

		if (logger.isDebugEnabled()) {
			String msg = String.format("Request validation: path=%s, sessionKey=%s, apiKey=%s, mandatory=%s",
					req.getPathInfo(), sessionKey, apiKey, mandatory);
			logger.debug(msg);
		}

		// If this is a login request we allow it to go through to complete
		// authentication.
		if ("/api/v1/session".equals(req.getPathInfo())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Letting login request through...");
			}
			return Authentication.NOT_CHECKED;
		}

		// If this is a CORS options request, we all it to go through so that
		// the browser can do a pre-flight check.
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Letting content request through: " + req.getPathInfo());
			}
			return Authentication.NOT_CHECKED;
		}

		// If this is a content request we similarly allow it to go through
		// so we can serve up web content requests (e.g., to our Angular 2
		// application).
		if (req.getPathInfo().startsWith("/content")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Letting content request through: " + req.getPathInfo());
			}
			return Authentication.NOT_CHECKED;
		}

		// Checks for supported authentication types in order.
		UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
		if (sessionKey != null) {
			try {
				// If we have a session key, prefer that.
				SessionData sessionData = um.validateSessionKey(sessionKey);
				UserIdentity identity = createIdentity(sessionData.getUserId().toString());
				return new UserAuthentication(Constraint.ANY_AUTH, identity);
			} catch (UnauthorizedException e) {
				logger.warn(String.format("No session available: token=%s, message=%s", sessionKey, e.getMessage()));
				if (logger.isDebugEnabled()) {
					logger.debug("Extended session failure information", e);
				}
				try {
					res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} catch (IOException e2) {
					if (logger.isDebugEnabled()) {
						logger.debug("Unable to send error message", e2);
					}
				}
				return Authentication.SEND_FAILURE;
			}
		} else if (apiKey != null) {
			try {
				io.goldfin.admin.service.api.model.User user = um.validateApiKeySecret(apiKey);
				UserIdentity identity = createIdentity(user.getId().toString());
				return new UserAuthentication(Constraint.ANY_AUTH, identity);
			} catch (UnauthorizedException e) {
				logger.warn(String.format("No API key found: key=%s, message=%s", sessionKey, e.getMessage()));
				if (logger.isDebugEnabled()) {
					logger.debug("Extended session failure information", e);
				}
				try {
					res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} catch (IOException e2) {
					if (logger.isDebugEnabled()) {
						logger.debug("Unable to send error message", e2);
					}
				}
				return Authentication.SEND_FAILURE;
			}
		} else {
			try {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return Authentication.SEND_FAILURE;
			} catch (IOException e) {
				throw new ServerAuthException(e);
			}
		}
	}

	private UserIdentity createIdentity(String userId) {
		Subject subject = new Subject();
		AbstractLoginService.UserPrincipal userPrincipal = new AbstractLoginService.UserPrincipal(userId, null);
		subject.getPrincipals().add(userPrincipal);
		subject.getPrincipals().add(new AbstractLoginService.RolePrincipal("user"));
		UserIdentity identity = new DefaultUserIdentity(subject, userPrincipal, new String[] { "user" });
		return identity;
	}
}