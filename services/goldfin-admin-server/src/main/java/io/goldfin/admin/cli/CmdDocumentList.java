/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdDocumentList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdDocumentList() {
	}

	public String getName() {
		return "document-list";
	}

	public String getDescription() {
		return "List documents";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		MinimalRestClient client = ctx.getRestClient();
		try {
			Document[] documents = client.get("/document", new Document[0].getClass());
			String documentListing = JsonHelper.writeToString(documents);
			System.out.println(documentListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}