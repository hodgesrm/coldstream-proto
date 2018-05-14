/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import joptsimple.OptionParser;

public class CmdDocumentScan implements Command {
	private OptionParser parser = new OptionParser();

	public CmdDocumentScan() {
		parser.accepts("id", "Document ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "document-scan";
	}

	public String getDescription() {
		return "Scan a document";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.post(String.format("/document/%s/scan", id), null, null);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}