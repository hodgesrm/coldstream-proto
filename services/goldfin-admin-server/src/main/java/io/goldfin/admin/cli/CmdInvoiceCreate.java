/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.ByteArrayInputStream;
import java.io.File;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.http.RestRequest;
import io.goldfin.admin.http.RestResponse;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdInvoiceCreate implements Command {
	private OptionParser parser = new OptionParser();

	public CmdInvoiceCreate() {
		parser.accepts("file", "Invoice PDF file").withRequiredArg().ofType(File.class);
		parser.accepts("description", "Invoice description").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "invoice-create";
	}

	public String getDescription() {
		return "Upload an invoice";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options and assign parameters to create tenant.
		File file = (File) CliUtils.requiredOption(ctx.options(), "file");
		String description = (String) ctx.options().valueOf("description");

		// Ensure file exists and is readable.
		if (!file.canRead()) {
			throw new CommandError(String.format("File is not readable or not found: %s", file.getAbsolutePath()));
		}

		// Create the tenant and load file as form data.
		MinimalRestClient client = ctx.getRestClient();
		try {
			RestRequest request = new RestRequest().POST().path("/invoice").multipart().addFile("file", file);
			if (description != null) {
				request.addText("description", description);
			}
			RestResponse response = client.execute(request);
			if (response.isError()) {
				throw new RestException(response.getCode(), response.getReason());
			}
			InvoiceEnvelope invoiceEnvelope = JsonHelper.readFromStream(new ByteArrayInputStream(response.getContent()),
					InvoiceEnvelope.class);
			String envelopeAsString = JsonHelper.writeToString(invoiceEnvelope);
			System.out.println(envelopeAsString);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}