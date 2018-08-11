package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.HostApiService;
import io.goldfin.admin.service.api.service.factories.HostApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.Host;

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

@Path("/host")


@io.swagger.annotations.Api(description = "the host API")

public class HostApi  {
   private final HostApiService delegate;

   public HostApi(@Context ServletConfig servletContext) {
      HostApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("HostApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (HostApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = HostApiServiceFactory.getHostApi();
      }

      this.delegate = delegate;
   }

    @DELETE
    @Path("/{id}")
    
    
    @io.swagger.annotations.ApiOperation(value = "Delete host record", notes = "Delete a host record.  It can be recreated by rescanning the corresponding document", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response hostDelete(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.hostDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show single host inventory record", notes = "Returns the most recent inventory record for a specific host.  The host must be identified by the resource ID or internal ID", response = Host.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Host.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Host.class) })
    public Response hostShow(@ApiParam(value = "Host resource ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.hostShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List current host inventory records", notes = "Return a list of current hosts in inventory.  This returns the most recent record for each host.", response = Host.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = Host.class, responseContainer = "List") })
    public Response hostShowAll(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.hostShowAll(securityContext);
    }
}
