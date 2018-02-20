/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdInvoiceList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdInvoiceList() {
		parser.accepts("full", "Return invoice items if true, otherwise only header").withRequiredArg()
				.ofType(Boolean.class);
	}

	public String getName() {
		return "invoice-list";
	}

	public String getDescription() {
		return "List invoices";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		Boolean full = (Boolean) ctx.options().valueOf("full");

		String path;
		if (full == null) {
			path = "/invoice";
		} else {
			path = String.format("/invoice?full=%s", full.toString());
		}

		MinimalRestClient client = ctx.getRestClient();
		try {
			Invoice[] invoices = client.get(path, new Invoice[0].getClass());
			String invoiceListing = JsonHelper.writeToString(invoices);
			System.out.println(invoiceListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}