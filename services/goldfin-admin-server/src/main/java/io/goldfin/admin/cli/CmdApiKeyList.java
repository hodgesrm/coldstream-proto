/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.service.api.model.ApiKey;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdApiKeyList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdApiKeyList() {
		parser.accepts("userId", "User ID (defaults to current user)").withRequiredArg().ofType(String.class)
				.defaultsTo(UserManager.CURRENT_USER_ID);
	}

	public String getName() {
		return "apikey-list";
	}

	public String getDescription() {
		return "List API keys";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String userId = (String) CliUtils.requiredOption(ctx.options(), "userId");
		MinimalRestClient client = ctx.getRestClient();
		try {
			ApiKey[] data = client.get(String.format("/user/%s/apikey", userId), new ApiKey[0].getClass());
			String dataListing = JsonHelper.writeToString(data);
			System.out.println(dataListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}