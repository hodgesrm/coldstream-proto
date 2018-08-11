package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.TenantApiService;
import io.goldfin.admin.service.api.service.factories.TenantApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.ModelApiResponse;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.TenantParameters;

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

@Path("/tenant")


@io.swagger.annotations.Api(description = "the tenant API")

public class TenantApi  {
   private final TenantApiService delegate;

   public TenantApi(@Context ServletConfig servletContext) {
      TenantApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("TenantApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (TenantApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = TenantApiServiceFactory.getTenantApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new tenant", notes = "Upload a new tenant registration request.", response = Tenant.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = Tenant.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Tenant creation failed", response = Tenant.class) })
    public Response tenantCreate(@ApiParam(value = "Tenant creation parameters" ,required=true) TenantParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tenantCreate(body,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a tenant", notes = "Delete a single tenant", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response tenantDelete(@ApiParam(value = "Tenant ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tenantDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show a single tenant", notes = "Return all information relative to a single tenant", response = Tenant.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Tenant.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Tenant.class) })
    public Response tenantShow(@ApiParam(value = "Tenant ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tenantShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List tenants", notes = "Return a list of all tenants", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = void.class) })
    public Response tenantShowall(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tenantShowall(securityContext);
    }
    @PUT
    @Path("/{id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Update a tenant", notes = "Update invoice description and tags. Changes to other fields are ignored", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response tenantUpdate(@ApiParam(value = "Tenant ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Tenant parameters" ) TenantParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tenantUpdate(id,body,securityContext);
    }
}
