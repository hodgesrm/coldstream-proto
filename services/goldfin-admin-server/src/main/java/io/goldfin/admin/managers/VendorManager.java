/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.security.Principal;
import java.util.List;

import io.goldfin.admin.data.tenant.VendorDataService;
import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.data.Session;

/**
 * Handles operations related to vendors.
 */
public class VendorManager implements Manager {
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

	public void deleteVendor(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		VendorDataService vendorService = new VendorDataService();
		try (Session session = context.tenantSession(tenantId).enlist(vendorService)) {
			int rows = vendorService.delete(id);
			session.commit();
			if (rows == 0) {
				throw new EntityNotFoundException("Vendor does not exist");
			}
		}
	}

	public Vendor getVendor(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		VendorDataService vendorDataService = new VendorDataService();
		try (Session session = context.tenantSession(tenantId).enlist(vendorDataService)) {
			Vendor vendor = vendorDataService.get(id);
			if (vendor == null) {
				throw new EntityNotFoundException("Vendor does not exist");
			}
			return vendor;
		}
	}

	public List<Vendor> getAllVendors(Principal principal) {
		String tenantId = getTenantId(principal);
		VendorDataService vendorService = new VendorDataService();
		try (Session session = context.tenantSession(tenantId).enlist(vendorService)) {
			return vendorService.getAll();
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