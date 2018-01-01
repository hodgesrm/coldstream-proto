/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.IOException;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

/**
 * Implements login.
 */
public class CmdInvoiceList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdInvoiceList() {
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
		MinimalRestClient client = ctx.getRestClient();
		try {
			InvoiceEnvelope[] invoices = client.get("/invoice", new InvoiceEnvelope[0].getClass());
			String invoiceListing = JsonHelper.writeToString(invoices);
			System.out.println(invoiceListing);
		} catch (IOException e) {
			throw new CommandError("Unable to print JSON");
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}