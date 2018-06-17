/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.http.RestRequest;
import io.goldfin.admin.http.RestResponse;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.utilities.JsonHelper;
import io.goldfin.shared.utilities.SerializationException;
import joptsimple.OptionParser;

public class CmdExtractDownload implements Command {
	static final Logger logger = LoggerFactory.getLogger(CmdExtractDownload.class);
	private OptionParser parser = new OptionParser();

	public CmdExtractDownload() {
		parser.accepts("extract-type", "Name of the extract").withRequiredArg().ofType(String.class);
		parser.accepts("query", "Query string to filter results").withRequiredArg().ofType(String.class);
		parser.accepts("output", "Extract output format").withRequiredArg().ofType(String.class).defaultsTo("csv");
	}

	public String getName() {
		return "extract-download";
	}

	public String getDescription() {
		return "Run an extract and download result";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String extractType = (String) CliUtils.requiredOption(ctx.options(), "extract-type");
		String query = (String) ctx.options().valueOf("query");
		String output = (String) ctx.options().valueOf("output");

		// Create the tenant and load file as form data.
		MinimalRestClient client = ctx.getRestClient();
		try {
			RestRequest request = new RestRequest().GET().path("/extract/download");
			request.queryParam("extractType", extractType);
			if (query != null) {
				request.queryParam("query", query);
			}
			request.queryParam("output", output);
			RestResponse response = client.execute(request);
			if (response.isError()) {
				throw generateRestException(response);
			}

			// Fetch out file name from Content-Disposition header.
			String contentDisposition = response.getHeader("Content-Disposition");
			String fileName = "data.csv";
			for (String disp : contentDisposition.split("; ")) {
				if (disp.startsWith("filename=") && disp.length() > 9) {
					fileName = disp.substring(9);
					break;
				}
			}
			File outName = new File(fileName);
			try (FileOutputStream fos = new FileOutputStream(outName)) {
				fos.write(response.getContent());
			}
			System.out.println(String.format("Wrote output file: %s", outName));
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} catch (IOException e) {
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