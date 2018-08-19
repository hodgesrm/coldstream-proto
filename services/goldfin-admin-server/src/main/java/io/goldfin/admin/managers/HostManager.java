/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.security.Principal;
import java.util.List;

import io.goldfin.admin.data.tenant.HostDataService;
import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.data.Session;

/**
 * Handles operations related to hosts.
 */
public class HostManager implements Manager {
	private ManagementContext context;

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	@Override
	public void prepare() {
		// Do nothing for now.
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	public void deleteHost(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		HostDataService hostService = new HostDataService();
		try (Session session = context.tenantSession(tenantId).enlist(hostService)) {
			int rows = hostService.delete(id);
			session.commit();
			if (rows == 0) {
				throw new EntityNotFoundException("Host does not exist");
			}
		}
	}

	public Host getHost(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		HostDataService hostDataService = new HostDataService();
		try (Session session = context.tenantSession(tenantId).enlist(hostDataService)) {
			Host host = hostDataService.get(id);
			if (host == null) {
				throw new EntityNotFoundException("Host does not exist");
			}
			return host;
		}
	}

	public List<Host> getLatestHosts(Principal principal) {
		String tenantId = getTenantId(principal);
		HostDataService hostService = new HostDataService();
		try (Session session = context.tenantSession(tenantId).enlist(hostService)) {
			return hostService.getLatest();
		}
	}

	/** Get the tenant ID for this user. */
	private String getTenantId(Principal principal) {
		UserManager userManager = ManagerRegistry.getInstance().getManager(UserManager.class);
		User user = userManager.getUser(principal.getName());
		if (user == null) {
			throw new RuntimeException(String.format("Unknown user: %s", principal.getName()));
		} else {
			return user.getTenantId().toString();
		}
	}
}