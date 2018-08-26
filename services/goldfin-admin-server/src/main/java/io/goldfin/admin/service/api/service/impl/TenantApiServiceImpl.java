/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.auth.AuthorizationChecks;
import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.TenantManager;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.TenantParameters;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.NotFoundException;
import io.goldfin.admin.service.api.service.TenantApiService;

/**
 * Perform operations on tenants.
 */
public class TenantApiServiceImpl extends TenantApiService {
	static private final Logger logger = LoggerFactory.getLogger(TenantApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response tenantCreate(TenantParameters body, SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyTenant(securityContext);
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			Tenant tenant = tm.createTenant(body);
			return Response.ok().entity(tenant).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response tenantDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyTenant(securityContext);
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			tm.deleteTenant(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response tenantShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyTenant(securityContext);
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			Tenant tenant = tm.getTenant(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(tenant).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response tenantShowall(SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyTenant(securityContext);
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			List<Tenant> tenants = tm.getAllTenants();
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(tenants).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response tenantUpdate(String id, TenantParameters body, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}
