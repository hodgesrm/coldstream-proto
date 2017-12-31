/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.IOException;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

/**
 * Implements login.
 */
public class CmdTenantList implements Command {
	private OptionParser parser = new OptionParser();

	public CmdTenantList() {
	}

	public String getName() {
		return "tenant-list";
	}

	public String getDescription() {
		return "List tenants";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		MinimalRestClient client = ctx.getRestClient();
		try {
			Tenant[] tenants = client.get("/tenant", new Tenant[0].getClass());
			String tenantListing = JsonHelper.writeToString(tenants);
			System.out.println(tenantListing);
		} catch (IOException e) {
			throw new CommandError("Unable to print JSON");
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}