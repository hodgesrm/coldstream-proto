package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.VendorApiService;
import io.goldfin.admin.service.api.service.factories.VendorApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.ModelApiResponse;
import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.model.VendorParameters;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/vendor")


@io.swagger.annotations.Api(description = "the vendor API")

public class VendorApi  {
   private final VendorApiService delegate;

   public VendorApi(@Context ServletConfig servletContext) {
      VendorApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("VendorApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (VendorApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = VendorApiServiceFactory.getVendorApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new vendor", notes = "Upload a new vendor definition.  Vendors are also created automatically if a vendor invoice is processed.", response = Vendor.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "vendor", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = Vendor.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Vendor creation failed", response = Vendor.class) })
    public Response vendorCreate(@ApiParam(value = "Vendor registration request parameters" ,required=true) VendorParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.vendorCreate(body,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a vendor", notes = "Delete a single vendor.  This can only be done if the vendor is not attached to invoices or existing inventory.", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "vendor", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid Request", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response vendorDelete(@ApiParam(value = "vendor ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.vendorDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show a single vendor", notes = "Return all information relative to a single vendor", response = Vendor.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "vendor", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Vendor.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Vendor.class) })
    public Response vendorShow(@ApiParam(value = "Vendor ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.vendorShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List vendors", notes = "Return a list of all vendors", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "vendor", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = void.class) })
    public Response vendorShowall(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.vendorShowall(securityContext);
    }
    @PUT
    @Path("/{id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Update a vendor", notes = "Update vendor description.", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "vendor", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response vendorUpdate(@ApiParam(value = "Vendor ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Vendor parameters" ) VendorParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.vendorUpdate(id,body,securityContext);
    }
}
