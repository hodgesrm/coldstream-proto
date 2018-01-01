/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.IOException;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdUserList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdUserList() {
	}

	public String getName() {
		return "user-list";
	}

	public String getDescription() {
		return "List users";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		MinimalRestClient client = ctx.getRestClient();
		try {
			User[] users = client.get("/user", new User[0].getClass());
			String userListing = JsonHelper.writeToString(users);
			System.out.println(userListing);
		} catch (IOException e) {
			throw new CommandError("Unable to print JSON");
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}