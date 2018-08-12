/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.http.RestRequest;
import io.goldfin.admin.http.RestResponse;
import io.goldfin.admin.restapi.jetty.SecurityAuthenticator;
import io.goldfin.admin.service.api.model.LoginCredentials;
import joptsimple.OptionParser;

/**
 * Implements login.
 */
public class CmdLogin implements Command {
	private OptionParser parser = new OptionParser();

	public CmdLogin() {
		parser.accepts("user", "User name").withRequiredArg().ofType(String.class);
		parser.accepts("password", "Password").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "login";
	}

	public String getDescription() {
		return "Login to server";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options.
		String host = (String) CliUtils.requiredOption(ctx.options(), "host");
		int port = (Integer) CliUtils.requiredOption(ctx.options(), "port");
		String user = (String) CliUtils.requiredOption(ctx.options(), "user");
		String password = (String) CliUtils.requiredOption(ctx.options(), "password");

		// Instantiate credentials.
		LoginCredentials credentials = new LoginCredentials();
		credentials.setUser(user);
		credentials.setPassword(password);

		// Login and get the header. Unlike other commands we have to instantiate the
		// client directly since there is no session available.
		MinimalRestClient client = new MinimalRestClient().host(host).port(port).prefix("/api/v1")
				.verbose(ctx.options().has("verbose")).connect();
		try {
			RestRequest request = new RestRequest().POST().path("/session").content(credentials);
			RestResponse response = client.execute(request);
			if (response.isError()) {
				throw new CommandError(
						String.format("Login failed: code=%d, reason=%s", response.getCode(), response.getReason()));
			}
			String token = response.getHeader(SecurityAuthenticator.SESSION_KEY_HEADER);

			// Save session.
			Session session = new Session();
			session.setHost(host);
			session.setPort(port);
			session.setUsername(user);
			session.setToken(token);
			ctx.setSession(session);
			System.out.println("Login succeeded");
		} catch (RestException e) {
			throw new CommandError("Rest command failed: " + e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}