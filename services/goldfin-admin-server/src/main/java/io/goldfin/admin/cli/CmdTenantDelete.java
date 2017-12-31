/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

/**
 * Implements login.
 */
public class CmdTenantDelete implements Command {
	private OptionParser parser = new OptionParser();

	public CmdTenantDelete() {
		parser.accepts("id", "Tenant ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "tenant-delete";
	}

	public String getDescription() {
		return "Delete a tenant";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.delete(String.format("/tenant/%s", id));
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}