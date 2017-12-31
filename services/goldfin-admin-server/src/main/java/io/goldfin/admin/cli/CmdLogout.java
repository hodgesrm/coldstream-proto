/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.goldfin.admin.exceptions.CommandError;
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
		Session session = ctx.getSession();
		if (session == null) {
			System.out.println("No current session, logout ignored");
		} else {
			MinimalRestClient client = ctx.getRestClient();
			try {
				// Must URL-encode token to ensure value gets through properly. 
				String encodedToken = URLEncoder.encode(session.getToken(), "UTF-8");
				client.delete(String.format("/session/%s", encodedToken));
			} catch (RestException e) {
				// Ignore.
			} catch (UnsupportedEncodingException e) {
				// Should not be possible. 
				throw new CommandError("Unsuppored UTF-8 encoding", e);
			} finally {
				client.close();
				ctx.clearSession();
				System.out.println("Logout succeeded");
			}
		}
	}
}