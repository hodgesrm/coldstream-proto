/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.http.RestException;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.TenantParameters;
import io.goldfin.shared.utilities.JsonHelper;
import joptsimple.OptionParser;

public class CmdTenantCreate implements Command {
	private OptionParser parser = new OptionParser();

	public CmdTenantCreate() {
		parser.accepts("name", "Tenant name").withRequiredArg().ofType(String.class);
		parser.accepts("description", "Tenant description").withRequiredArg().ofType(String.class);
	}

	public String getName() {
		return "tenant-create";
	}

	public String getDescription() {
		return "Create a new tenant";
	}

	public OptionParser getOptParser() {
		return parser;
	}

	public void exec(CliContext ctx) {
		// Check options and assign parameters to create tenant.
		String name = (String) CliUtils.requiredOption(ctx.options(), "name");
		String description = (String) ctx.options().valueOf("description");

		TenantParameters params = new TenantParameters();
		params.setName(name);
		params.setDescription(description);

		// Create the tenant.
		MinimalRestClient client = ctx.getRestClient();
		try {
			Tenant tenant = client.post("/tenant", params, Tenant.class);
			String tenantListing = JsonHelper.writeToString(tenant);
			System.out.println(tenantListing);
		} catch (RestException e) {
			throw new CommandError(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}