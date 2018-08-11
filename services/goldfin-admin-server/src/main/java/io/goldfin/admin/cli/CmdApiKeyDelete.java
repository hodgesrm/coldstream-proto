/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.managers.UserManager;
import joptsimple.OptionParser;

public class CmdApiKeyDelete implements Command {
	private OptionParser parser = new OptionParser();

	public CmdApiKeyDelete() {
		parser.accepts("userId", "User ID (defaults to current user)").withRequiredArg().ofType(String.class)
				.defaultsTo(UserManager.CURRENT_USER_ID);
		parser.accepts("id", "API key ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "apikey-delete";
	}

	public String getDescription() {
		return "Delete an API key";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String userId = (String) CliUtils.requiredOption(ctx.options(), "userId");
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.delete(String.format("/user/%s/apikey/%s", userId, id));
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}