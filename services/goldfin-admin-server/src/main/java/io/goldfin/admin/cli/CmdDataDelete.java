/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

public class CmdDataDelete implements Command {
	private OptionParser parser = new OptionParser();

	public CmdDataDelete() {
		parser.accepts("id", "Data series ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "data-delete";
	}

	public String getDescription() {
		return "Delete a data";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.delete(String.format("/data/%s", id));
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}