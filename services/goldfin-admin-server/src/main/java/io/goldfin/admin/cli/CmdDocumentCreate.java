/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.tenant.ExtendedTagSet;
import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.http.RestRequest;
import io.goldfin.admin.http.RestResponse;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.utilities.JsonHelper;
import io.goldfin.shared.utilities.SerializationException;
import joptsimple.OptionParser;

public class CmdDocumentCreate implements Command {
	static final Logger logger = LoggerFactory.getLogger(CmdDocumentCreate.class);
	private OptionParser parser = new OptionParser();

	public CmdDocumentCreate() {
		parser.accepts("file", "Document file").withRequiredArg().ofType(File.class);
		parser.accepts("description", "Document description").withRequiredArg().ofType(String.class);
		parser.accepts("process", "If true automatically scan document").withRequiredArg().ofType(Boolean.class)
				.defaultsTo(true);
		parser.accepts("tags", "Tags in form key1:value1,key2:value2,...").withRequiredArg().ofType(String.class)
				.withValuesSeparatedBy(",");
	}

	public String getName() {
		return "document-create";
	}

	public String getDescription() {
		return "Upload a document";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options and assign parameters to create tenant.
		File file = (File) CliUtils.requiredOption(ctx.options(), "file");
		String description = (String) ctx.options().valueOf("description");
		Boolean process = (Boolean) ctx.options().valueOf("process");
		@SuppressWarnings("unchecked")
		List<String> tagValueList = (List<String>) ctx.options().valuesOf("tags");

		// Ensure file exists and is readable.
		if (!file.canRead()) {
			throw new CommandError(String.format("File is not readable or not found: %s", file.getAbsolutePath()));
		}

		// Convert tags to TagSet and serialize to JSON since this is form data and
		// won't accept entities.
		String serializedTags = null;
		if (tagValueList != null) {
			ExtendedTagSet tags = ExtendedTagSet.fromNameValueList(tagValueList);
			serializedTags = JsonHelper.writeToStringOrNull(tags);
		}

		// Create the tenant and load file as form data.
		MinimalRestClient client = ctx.getRestClient();
		try {
			RestRequest request = new RestRequest().POST().path("/document").multipart().addFile("file", file);
			if (description != null) {
				request.addText("description", description);
			}
			if (serializedTags != null) {
				request.addText("tags", serializedTags);
			}
			if (process != null) {
				request.addText("process", process.toString());
			}
			RestResponse response = client.execute(request);
			if (response.isError()) {
				throw generateRestException(response);
			}
			Document document = JsonHelper.readFromStream(new ByteArrayInputStream(response.getContent()),
					Document.class);
			String envelopeAsString = JsonHelper.writeToString(document);
			System.out.println(envelopeAsString);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}

	private RestException generateRestException(RestResponse response) throws RestException {
		// Try to get the error message.
		String message = null;
		try {
			ApiResponseMessage apiResponse = JsonHelper.readFromStream(new ByteArrayInputStream(response.getContent()),
					ApiResponseMessage.class);
			message = apiResponse.getMessage();
		} catch (SerializationException e) {
			logger.debug("Unable to deserialize error response", e);
		}
		throw new RestException(response.getCode(), response.getReason(), message);
	}

}