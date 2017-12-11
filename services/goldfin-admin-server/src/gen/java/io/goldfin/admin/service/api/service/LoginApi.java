package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.LoginApiService;
import io.goldfin.admin.service.api.service.factories.LoginApiServiceFactory;

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

@Path("/login")


@io.swagger.annotations.Api(description = "the login API")

public class LoginApi  {
   private final LoginApiService delegate;

   public LoginApi(@Context ServletConfig servletContext) {
      LoginApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("LoginApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (LoginApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = LoginApiServiceFactory.getLoginApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Login to system", notes = "Obtain API key using login credentials", response = void.class, tags={ "security", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Tenant creation failed", response = void.class) })
    public Response loginByCredentials(@ApiParam(value = "Login credentials" ,required=true) LoginCredentials body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.loginByCredentials(body,securityContext);
    }
}
