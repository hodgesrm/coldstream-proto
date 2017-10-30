//  ========================================================================
//  Copyright (c) 2017 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.invoice.restapi.jetty;

import java.io.IOException;
import java.util.Enumeration;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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

/**
 * Checks requests to ensure they are authorized.
 */
public class SecurityAuthenticator implements Authenticator {
	private static final Logger logger = LoggerFactory.getLogger(SecurityAuthenticator.class);
	public static final String API_KEY_HEADER = "vnd.io.goldfin.session";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		logger.info("Filtering request");
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			logger.info(String.format("%s: %s", name, request.getAttribute(name)));
		}
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String name = names.nextElement();
			logger.info(String.format("%s", name));
		}

		HttpServletRequest r = (HttpServletRequest) request;
		logger.info("Path Info: " + r.getPathInfo());
		logger.info("Context Path: " + r.getContextPath());
		logger.info("Servlet path: " + r.getServletPath());
		logger.info("Header: " + r.getHeader("vnd-io-goldfin-auth"));
	}

	public String getAuthMethod() {
		logger.info("Returning auth method: " + this.getClass().getSimpleName());
		return Constraint.ANY_AUTH;
	}

	public void prepareRequest(ServletRequest arg0) {
		logger.info("Preparing request: " + this.getClass().getSimpleName());
	}

	public boolean secureResponse(ServletRequest arg0, ServletResponse arg1, boolean arg2, User arg3)
			throws ServerAuthException {
		logger.info("Checking for secure response: " + this.getClass().getSimpleName());
		return false;
	}

	public void setConfiguration(AuthConfiguration arg0) {
		logger.info("Setting auth configuration: " + this.getClass().getSimpleName());
	}

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

		// If the apiKey is missing, we aren't authenticated.
		if (apiKey == null) {
			try {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return Authentication.SEND_FAILURE;
			} catch (IOException e) {
				throw new ServerAuthException(e);
			}
		} else {
			return userAuthentication(apiKey);
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
		UserIdentity identity = new DefaultUserIdentity(subject, userPrincipal, new String[] {"user"});
		return new UserAuthentication(Constraint.ANY_AUTH, identity);
	}
}