/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.service.api.model.ApiKey;
import io.goldfin.admin.service.api.model.ApiKeyParameters;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdApiKeyCreate implements Command {
	private OptionParser parser = new OptionParser();

	public CmdApiKeyCreate() {
		parser.accepts("userId", "User ID (defaults to current user)").withRequiredArg().ofType(String.class)
				.defaultsTo(UserManager.CURRENT_USER_ID);
		parser.accepts("name", "API key name").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "apikey-create";
	}

	public String getDescription() {
		return "Creates a new API key";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String userId = (String) CliUtils.requiredOption(ctx.options(), "userId");
		String name = (String) CliUtils.requiredOption(ctx.options(), "name");

		ApiKeyParameters params = new ApiKeyParameters();
		params.setName(name);

		MinimalRestClient client = ctx.getRestClient();
		try {
			ApiKey apiKey = client.post(String.format("/user/%s/apikey", userId), params, ApiKey.class);
			String apiKeyListing = JsonHelper.writeToString(apiKey);
			System.out.println(apiKeyListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}