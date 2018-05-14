/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

public class CmdDataProcess implements Command {
	private OptionParser parser = new OptionParser();

	public CmdDataProcess() {
		parser.accepts("id", "Data series ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "data-process";
	}

	public String getDescription() {
		return "Submit data series for analytical processing";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.post(String.format("/data/%s/process", id), null, null);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}