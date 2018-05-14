package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.DataApiService;
import io.goldfin.admin.service.api.service.factories.DataApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.DataSeries;
import java.io.File;
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

@Path("/data")


@io.swagger.annotations.Api(description = "the data API")

public class DataApi  {
   private final DataApiService delegate;

   public DataApi(@Context ServletConfig servletContext) {
      DataApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("DataApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (DataApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = DataApiServiceFactory.getDataApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Upload data series", notes = "Upload data series in a file for analysis", response = DataSeries.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = DataSeries.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Creation failed", response = DataSeries.class) })
    public Response dataCreate(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataCreate(fileInputStream, fileDetail,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a data series", notes = "Delete a data series and any derived information", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response dataDelete(@ApiParam(value = "Series ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Return data series content", notes = "Download data series content", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response dataFetchContent(@ApiParam(value = "Series ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataFetchContent(id,securityContext);
    }
    @POST
    @Path("/{id}/process")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Kick off background processing of data series", notes = "Run background processing of data series, which may generate one or more inventory records.", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response dataProcess(@ApiParam(value = "Series ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataProcess(id,securityContext);
    }
    @HEAD
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Return data series metadata", notes = "Download data series metadata without content", response = DataSeries.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = DataSeries.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = DataSeries.class) })
    public Response dataShow(@ApiParam(value = "Series ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List data serties", notes = "Return a list of metadata entries for all data series", response = DataSeries.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "inventory", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = DataSeries.class, responseContainer = "List") })
    public Response dataShowAll(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.dataShowAll(securityContext);
    }
}