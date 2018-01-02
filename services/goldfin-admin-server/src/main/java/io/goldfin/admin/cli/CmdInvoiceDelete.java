/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

public class CmdInvoiceDelete implements Command {
	private OptionParser parser = new OptionParser();

	public CmdInvoiceDelete() {
		parser.accepts("id", "Invoice ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "invoice-delete";
	}

	public String getDescription() {
		return "Delete an invoice";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.delete(String.format("/invoice/%s", id));
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}