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

public class CmdDocumentDownload implements Command {
	static final Logger logger = LoggerFactory.getLogger(CmdDocumentDownload.class);
	private OptionParser parser = new OptionParser();

	public CmdDocumentDownload() {
		parser.accepts("id", "Document ID").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "document-download";
	}

	public String getDescription() {
		return "Download document";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");

		// Create the tenant and load file as form data.
		MinimalRestClient client = ctx.getRestClient();
		try {
			RestRequest request = new RestRequest().GET().path(String.format("/document/%s/download", id));
			RestResponse response = client.execute(request);
			if (response.isError()) {
				throw generateRestException(response);
			}

			// Fetch out file name from Content-Disposition header.
			String contentDisposition = response.getHeader("Content-Disposition");
			String fileName = "document.dat";
			for (String disp : contentDisposition.split("; ")) {
				if (disp.startsWith("filename=") && disp.length() > 9) {
					// File names are quoted, so we strip double quotes. 
					fileName = disp.substring(9).replace("\"", "");
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