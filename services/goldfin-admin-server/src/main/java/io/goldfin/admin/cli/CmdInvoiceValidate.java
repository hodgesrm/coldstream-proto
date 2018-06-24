/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.InvoiceValidationResult;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdInvoiceValidate implements Command {
	private OptionParser parser = new OptionParser();

	public CmdInvoiceValidate() {
		parser.accepts("onlyFailing", "If true return only failed validation results, otherwise all results")
				.withRequiredArg().ofType(Boolean.class).defaultsTo(true);
		parser.accepts("id", "Return invoice with this ID otherwise all").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "invoice-validate";
	}

	public String getDescription() {
		return "Validate invoice";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) CliUtils.requiredOption(ctx.options(), "id");
		Boolean onlyFailing = (Boolean) CliUtils.requiredOption(ctx.options(), "onlyFailing");

		String path = String.format("/invoice/%s/validate?onlyFailing=%s", id, onlyFailing.toString());

		MinimalRestClient client = ctx.getRestClient();
		try {
			InvoiceValidationResult[] results = client.post(path, null, new InvoiceValidationResult[0].getClass());
			String resultListing = JsonHelper.writeToString(results);
			System.out.println(resultListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}