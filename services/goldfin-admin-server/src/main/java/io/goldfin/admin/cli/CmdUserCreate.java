/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdUserCreate implements Command {
	private OptionParser parser = new OptionParser();

	public CmdUserCreate() {
		parser.accepts("user", "User name in form username@tenantname").withRequiredArg().ofType(String.class);
		parser.accepts("initialPassword", "Password for user").withRequiredArg().ofType(String.class);
		parser.accepts("roles", "Comma-separated list of user roles").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "user-create";
	}

	public String getDescription() {
		return "Create a new user";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options and assign parameters to create tenant.
		String username = (String) CliUtils.requiredOption(ctx.options(), "user");
		String initialPassword = (String) CliUtils.requiredOption(ctx.options(), "initialPassword");
		String roles = (String) ctx.options().valueOf("roles");

		UserParameters params = new UserParameters();
		params.setUser(username);
		params.setInitialPassword(initialPassword);
		params.setRoles(roles);

		// Create the tenant.
		MinimalRestClient client = ctx.getRestClient();
		try {
			User user = client.post("/user", params, User.class);
			String userListing = JsonHelper.writeToString(user);
			System.out.println(userListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}