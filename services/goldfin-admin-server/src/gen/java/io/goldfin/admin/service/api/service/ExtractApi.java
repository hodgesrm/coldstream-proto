package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.ExtractApiService;
import io.goldfin.admin.service.api.service.factories.ExtractApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.io.File;

import java.util.Map;
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

@Path("/extract")


@io.swagger.annotations.Api(description = "the extract API")

public class ExtractApi  {
   private final ExtractApiService delegate;

   public ExtractApi(@Context ServletConfig servletContext) {
      ExtractApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ExtractApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ExtractApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ExtractApiServiceFactory.getExtractApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/download")
    
    @Produces({ "text/csv", "text/json" })
    @io.swagger.annotations.ApiOperation(value = "Download a data extract", notes = "Extract data for a particular extract type, where these correspond to schema types as well as  reports that join data from multiple schema types", response = File.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "extract", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Input error", response = Void.class) })
    public Response extractDownload(@ApiParam(value = "Name of the extract type",required=true) @QueryParam("extractType") String extractType
,@ApiParam(value = "A query string that specifies extract content.  If omitted all entities are selected.") @QueryParam("filter") String filter
,@ApiParam(value = "Maximum number of entities to return") @QueryParam("limit") Integer limit
,@ApiParam(value = "A comma-separated list of order by columns of the form name1[:asc|desc],name2[:asc|desc],...  If sort order is omitted it defaults to asc (ascending).") @QueryParam("order") String order
,@ApiParam(value = "Selects the extract output type. Currently only CSV is supported.") @QueryParam("output") String output
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.extractDownload(extractType,filter,limit,order,output,securityContext);
    }
    @GET
    @Path("/types")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List available extract types", notes = "Return a list of available extrac types", response = String.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "extract", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = String.class, responseContainer = "List") })
    public Response extractTypes(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.extractTypes(securityContext);
    }
}
