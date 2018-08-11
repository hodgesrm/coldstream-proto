package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.SessionApiService;
import io.goldfin.admin.service.api.service.factories.SessionApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.model.ModelApiResponse;

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

@Path("/session")


@io.swagger.annotations.Api(description = "the session API")

public class SessionApi  {
   private final SessionApiService delegate;

   public SessionApi(@Context ServletConfig servletContext) {
      SessionApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("SessionApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (SessionApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = SessionApiServiceFactory.getSessionApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Login to system", notes = "Obtain API key using login credentials", response = void.class, tags={ "security", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad input", response = void.class) })
    public Response loginByCredentials(@ApiParam(value = "Login credentials" ,required=true) LoginCredentials body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.loginByCredentials(body,securityContext);
    }
    @DELETE
    @Path("/{token}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Logout from system", notes = "Delete session, which is no longer usable after this call", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "security", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response logout(@ApiParam(value = "Session ID token",required=true) @PathParam("token") String token
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.logout(token,securityContext);
    }
}
