package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.model.*;
import io.goldfin.front.invoice.api.service.HostApiService;
import io.goldfin.front.invoice.api.service.factories.HostApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.front.invoice.api.model.Host;
import io.goldfin.front.invoice.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

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

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new host inventory entry", notes = "Upload a host inventory entry", response = Host.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = Host.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Host creation failed", response = Host.class) })
    public Response inventoryHostCreate(@ApiParam(value = "The host definition" ,required=true) Host body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.inventoryHostCreate(body,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a host inventory record", notes = "Delete a host inventory record", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response inventoryHostDelete(@ApiParam(value = "Host ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.inventoryHostDelete(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List host entries", notes = "Return a list of all host inventory records", response = Host.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = Host.class, responseContainer = "List") })
    public Response inventoryHostShowAll(@ApiParam(value = "If true return complete invoice content, otherwise just the Invoice fields", defaultValue="true") @DefaultValue("true") @QueryParam("summary") Boolean summary
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.inventoryHostShowAll(summary,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show a single host inventory record", notes = "Return all information relative to a single host", response = Host.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Host.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Host.class) })
    public Response invoiceHostShow(@ApiParam(value = "Host ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceHostShow(id,securityContext);
    }
}
