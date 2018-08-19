/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdDataList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdDataList() {
		parser.accepts("id", "Document ID (lists all if omitted)").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "data-list";
	}

	public String getDescription() {
		return "List one or more data series";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		String id = (String) ctx.options().valueOf("id");
		MinimalRestClient client = ctx.getRestClient();
		try {
			if (id == null) {
				DataSeries[] datas = client.get("/data", new DataSeries[0].getClass());
				String dataListing = JsonHelper.writeToString(datas);
				System.out.println(dataListing);
			} else {
				DataSeries data = client.get(String.format("/data/%s", id), DataSeries.class);
				String dataListing = JsonHelper.writeToString(data);
				System.out.println(dataListing);
			}
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}