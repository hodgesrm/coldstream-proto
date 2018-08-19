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
		parser.accepts("id", "Document ID (lists all if omitted)").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "document-list";
	}

	public String getDescription() {
		return "List document(s)";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) ctx.options().valueOf("id");

		MinimalRestClient client = ctx.getRestClient();
		try {
			if (id == null) {
				Document[] documents = client.get("/document", new Document[0].getClass());
				String documentListing = JsonHelper.writeToString(documents);
				System.out.println(documentListing);
			} else {
				Document document = client.get(String.format("/document/%s", id), Document.class);
				String documentListing = JsonHelper.writeToString(document);
				System.out.println(documentListing);
			}
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}