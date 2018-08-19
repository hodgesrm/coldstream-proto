/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.tenant.ExtendedTagSet;
import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.DocumentParameters;
import joptsimple.OptionParser;

public class CmdDocumentUpdate implements Command {
	static final Logger logger = LoggerFactory.getLogger(CmdDocumentUpdate.class);
	private OptionParser parser = new OptionParser();

	public CmdDocumentUpdate() {
		parser.accepts("id", "Document ID").withRequiredArg().ofType(String.class);
		parser.accepts("description", "Document description").withRequiredArg().ofType(String.class);
		parser.accepts("tags", "Tags in form key1:value1,key2:value2,...").withRequiredArg().ofType(String.class)
				.withValuesSeparatedBy(",");
	}

	public String getName() {
		return "document-update";
	}

	public String getDescription() {
		return "Update document metadata";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options and assign parameters to create tenant.
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		String description = (String) ctx.options().valueOf("description");
		@SuppressWarnings("unchecked")
		List<String> tagValueList = (List<String>) ctx.options().valuesOf("tags");

		// Convert tags to TagSet and serialize to JSON. 
		boolean hasUpdate = false;
		DocumentParameters docParams = new DocumentParameters();
		if (tagValueList != null) {
			ExtendedTagSet tags = ExtendedTagSet.fromNameValueList(tagValueList);
			docParams.setTags(tags);
			hasUpdate = true;
		}
		if (description != null) {
			docParams.setDescription(description);
			hasUpdate = true;
		}
		if (! hasUpdate) {
			throw new CommandError("No update parameters supplied!");
		}
		
		// Create the tenant and load file as form data.
		MinimalRestClient client = ctx.getRestClient();
		try {
			client.update(String.format("/document/%s", id), docParams);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}