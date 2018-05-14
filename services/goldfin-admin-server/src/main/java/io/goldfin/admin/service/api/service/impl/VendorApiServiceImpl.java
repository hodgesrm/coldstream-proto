/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.VendorManager;
import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.model.VendorParameters;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.NotFoundException;
import io.goldfin.admin.service.api.service.VendorApiService;

/**
 * Implements operations on vendors.
 */
public class VendorApiServiceImpl extends VendorApiService {
	static private final Logger logger = LoggerFactory.getLogger(VendorApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response vendorCreate(VendorParameters body, SecurityContext securityContext) throws NotFoundException {
		return helper.toApiResponse(new UnsupportedOperationException("Not implemented"));
	}

	@Override
	public Response vendorDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			VendorManager vm = ManagerRegistry.getInstance().getManager(VendorManager.class);
			vm.deleteVendor(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response vendorShow(String id, SecurityContext securityContext) throws NotFoundException {
		return helper.toApiResponse(new UnsupportedOperationException("Not implemented"));
	}

	@Override
	public Response vendorShowall(SecurityContext securityContext) throws NotFoundException {
		try {
			VendorManager vm = ManagerRegistry.getInstance().getManager(VendorManager.class);
			List<Vendor> vendors = vm.getAllVendors(securityContext.getUserPrincipal());
			return Response.ok().entity(vendors).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response vendorUpdate(String id, VendorParameters body, SecurityContext securityContext)
			throws NotFoundException {
		return helper.toApiResponse(new UnsupportedOperationException("Not implemented"));
	}
}
