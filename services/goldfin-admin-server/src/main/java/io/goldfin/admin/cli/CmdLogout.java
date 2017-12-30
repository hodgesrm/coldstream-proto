/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

/**
 * Implements login.
 */
public class CmdLogout implements Command {
	private OptionParser parser = new OptionParser();

	public CmdLogout() {
	}

	public String getName() {
		return "logout";
	}

	public String getDescription() {
		return "Logout from server";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Logout and destroy the session.
		MinimalRestClient client = ctx.getRestClient();
		try {
			// Issue the logout request and collect token.
			client.post("/logout", null, null);
		} catch (RestException e) {
			// Ignore.
		} finally {
			client.close();
			ctx.clearSession();
		}
	}
}