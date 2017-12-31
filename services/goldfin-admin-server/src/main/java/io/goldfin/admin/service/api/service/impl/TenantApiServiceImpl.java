package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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
	@Override
	public Response tenantCreate(TenantParameters body, SecurityContext securityContext) throws NotFoundException {
		try {
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			Tenant tenant = tm.createTenant(body);
			return Response.ok().entity(tenant).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response tenantDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			tm.deleteTenant(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response tenantShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			Tenant tenant = tm.getTenant(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(tenant).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response tenantShowall(SecurityContext securityContext) throws NotFoundException {
		try {
			TenantManager tm = ManagerRegistry.getInstance().getManager(TenantManager.class);
			List<Tenant> tenants = tm.getAllTenants();
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(tenants).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response tenantUpdate(String id, TenantParameters body, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}
