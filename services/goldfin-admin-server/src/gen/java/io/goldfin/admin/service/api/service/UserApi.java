package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.UserApiService;
import io.goldfin.admin.service.api.service.factories.UserApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.ModelApiResponse;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.admin.service.api.model.UserPasswordParameters;

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

@Path("/user")


@io.swagger.annotations.Api(description = "the user API")

public class UserApi  {
   private final UserApiService delegate;

   public UserApi(@Context ServletConfig servletContext) {
      UserApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("UserApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (UserApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = UserApiServiceFactory.getUserApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new user for a tenant", notes = "Upload a new user registration request.", response = User.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = User.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "User creation failed", response = User.class) })
    public Response userCreate(@ApiParam(value = "User registration request parameters" ,required=true) UserParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userCreate(body,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a user", notes = "Delete a user", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response userDelete(@ApiParam(value = "User ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show a single user", notes = "Return all information relative to a single user", response = Tenant.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Tenant.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Tenant.class) })
    public Response userShow(@ApiParam(value = "User ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List users", notes = "Return a list of all users visible to current user", response = User.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = User.class, responseContainer = "List") })
    public Response userShowall(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userShowall(securityContext);
    }
    @PUT
    @Path("/{id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Update a user", notes = "Update user description", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response userUpdate(@ApiParam(value = "User ID",required=true) @PathParam("id") String id
,@ApiParam(value = "User parameters" ) UserParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userUpdate(id,body,securityContext);
    }
    @PUT
    @Path("/{id}/password")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Update user password", notes = "Sets a new user password", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "tenant", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response userUpdatePassword(@ApiParam(value = "User ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Password change parameters" ) UserPasswordParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.userUpdatePassword(id,body,securityContext);
    }
}
