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
import io.goldfin.admin.exceptions.NoSessionFoundException;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;

/**
 * Checks requests to ensure they are authorized.
 */
public class SecurityAuthenticator implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(SecurityAuthenticator.class);
	public static final String API_KEY_HEADER = "vnd.io.goldfin.session";

	@Override
	public String getAuthMethod() {
		logger.info("Returning auth method: " + this.getClass().getSimpleName());
		return Constraint.ANY_AUTH;
	}

	@Override
	public void prepareRequest(ServletRequest arg0) {
		logger.info("Preparing request: " + this.getClass().getSimpleName());
	}

	@Override
	public boolean secureResponse(ServletRequest arg0, ServletResponse arg1, boolean arg2, User arg3)
			throws ServerAuthException {
		logger.info("Checking for secure response: " + this.getClass().getSimpleName());
		return false;
	}

	@Override
	public void setConfiguration(AuthConfiguration arg0) {
		logger.info("Setting auth configuration: " + this.getClass().getSimpleName());
	}

	@Override
	public Authentication validateRequest(ServletRequest request, ServletResponse response, boolean mandatory)
			throws ServerAuthException {
		logger.info("Validating request: " + this.getClass().getSimpleName());

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Fetch API key value.
		String apiKey = req.getHeader(API_KEY_HEADER);

		String msg = String.format("Request validation: path=%s, header=%s, mandatory=%s", req.getPathInfo(),
				req.getHeader(API_KEY_HEADER), mandatory);
		logger.info(msg);

		// In this case we let the call through. Still figuring out how to do this.
		if (!mandatory) {
			// return new DeferredAuthentication(this);
			return Authentication.UNAUTHENTICATED;
		}

		// If this is a login request we allow it to go through to complete
		// authentication.
		if ("/api/v1/login".equals(req.getPathInfo())) {
			logger.info("Letting login request through...");
			return Authentication.NOT_CHECKED;
		}

		// If the apiKey is missing, we aren't authenticated.
		if (apiKey == null) {
			try {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return Authentication.SEND_FAILURE;
			} catch (IOException e) {
				throw new ServerAuthException(e);
			}
		} else {
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			try {
				SessionData sessionData = um.validate(apiKey);
				Subject subject = new Subject();
				AbstractLoginService.UserPrincipal userPrincipal = new AbstractLoginService.UserPrincipal(
						sessionData.getUserId().toString(), null);
				subject.getPrincipals().add(userPrincipal);
				subject.getPrincipals().add(new AbstractLoginService.RolePrincipal("user"));
				UserIdentity identity = new DefaultUserIdentity(subject, userPrincipal, new String[] { "user" });
				return new UserAuthentication(Constraint.ANY_AUTH, identity);
			} catch (NoSessionFoundException e) {
				logger.warn(String.format("No session available: token=%s, message=%s", apiKey, e.getMessage()));
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
		}
	}

	/**
	 * Look up user and create a user identity from the apiKey.
	 */
	private UserAuthentication userAuthentication(String apiKey) {
		// Dummy for now.
		Subject subject = new Subject();
		AbstractLoginService.UserPrincipal userPrincipal = new AbstractLoginService.UserPrincipal("dummy", null);
		subject.getPrincipals().add(userPrincipal);
		subject.getPrincipals().add(new AbstractLoginService.RolePrincipal("user"));
		UserIdentity identity = new DefaultUserIdentity(subject, userPrincipal, new String[] { "user" });
		return new UserAuthentication(Constraint.ANY_AUTH, identity);
	}
}