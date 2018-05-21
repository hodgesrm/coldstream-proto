/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.Host;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdHostList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdHostList() {
	}

	public String getName() {
		return "host-list";
	}

	public String getDescription() {
		return "List hosts in inventory";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		MinimalRestClient client = ctx.getRestClient();
		try {
			Host[] hosts = client.get("/host", new Host[0].getClass());
			String dataListing = JsonHelper.writeToString(hosts);
			System.out.println(dataListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}