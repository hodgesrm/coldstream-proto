package io.goldfin.front.invoice.api.service.impl;

import io.goldfin.front.invoice.api.service.*;
import io.goldfin.front.invoice.api.model.*;

import io.goldfin.front.invoice.api.model.ModelApiResponse;
import io.goldfin.front.invoice.api.model.Tenant;
import io.goldfin.front.invoice.api.model.TenantParameters;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public class TenantApiServiceImpl extends TenantApiService {
    @Override
    public Response tenantCreate(TenantParameters body, SecurityContext securityContext) throws NotFoundException {
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
