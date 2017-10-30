package io.goldfin.front.invoice.api.service.impl;

import java.security.Principal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.goldfin.front.invoice.api.model.TenantParameters;
import io.goldfin.front.invoice.api.model.TenantRegistrationParameters;
import io.goldfin.front.invoice.api.service.ApiResponseMessage;
import io.goldfin.front.invoice.api.service.NotFoundException;
import io.goldfin.front.invoice.api.service.TenantApiService;

public class TenantApiServiceImpl extends TenantApiService {
    @Override
    public Response tenantCreate(TenantRegistrationParameters body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response tenantDelete(String id, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response tenantShow(String id, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response tenantShowall(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response tenantUpdate(String id, TenantParameters body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
