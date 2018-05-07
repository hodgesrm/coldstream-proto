/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdVendorList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdVendorList() {
	}

	public String getName() {
		return "vendor-list";
	}

	public String getDescription() {
		return "List vendors";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		MinimalRestClient client = ctx.getRestClient();
		try {
			Vendor[] vendors = client.get("/vendor", new Vendor[0].getClass());
			String vendorListing = JsonHelper.writeToString(vendors);
			System.out.println(vendorListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}