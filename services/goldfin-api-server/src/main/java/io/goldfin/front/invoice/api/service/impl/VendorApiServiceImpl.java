package io.goldfin.front.invoice.api.service.impl;

import io.goldfin.front.invoice.api.service.*;
import io.goldfin.front.invoice.api.model.*;

import io.goldfin.front.invoice.api.model.ModelApiResponse;
import io.goldfin.front.invoice.api.model.Vendor;
import io.goldfin.front.invoice.api.model.VendorParameters;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public class VendorApiServiceImpl extends VendorApiService {
    @Override
    public Response vendorCreate(VendorParameters body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response vendorDelete(String id, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response vendorShow(String id, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response vendorShowall(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response vendorUpdate(String id, VendorParameters body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
