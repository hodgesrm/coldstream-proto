package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.DocumentApiService;
import io.goldfin.admin.service.api.service.factories.DocumentApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.DocumentParameters;
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

@Path("/document")


@io.swagger.annotations.Api(description = "the document API")

public class DocumentApi  {
   private final DocumentApiService delegate;

   public DocumentApi(@Context ServletConfig servletContext) {
      DocumentApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("DocumentApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (DocumentApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = DocumentApiServiceFactory.getDocumentApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Upload document", notes = "Upload a new document for scanning", response = Document.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = Document.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invoice creation failed", response = Document.class) })
    public Response documentCreate(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@ApiParam(value = "A optional description of the document")@FormDataParam("description")  String description
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentCreate(fileInputStream, fileDetail,description,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a invoice", notes = "Delete a single document and associated semantic content", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response documentDelete(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentDelete(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Return document content", notes = "Download document content", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response documentFetchContent(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentFetchContent(id,securityContext);
    }
    @POST
    @Path("/{id}/scan")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Kick off document scanning", notes = "Run background scanning on document.  The document state and semantic information will be updated when finished.", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response documentScan(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentScan(id,securityContext);
    }
    @HEAD
    @Path("/{id}")
    
    @Produces({ "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Return document metadata", notes = "Download document content", response = Document.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Document.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Document.class) })
    public Response documentShow(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List documents", notes = "Return a list of all documents", response = Document.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = Document.class, responseContainer = "List") })
    public Response documentShowAll(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentShowAll(securityContext);
    }
    @PUT
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update document information", notes = "Update document description and tags. Changes to other fields are ignored", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "APIKeyHeader")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response documentUpdate(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Document descriptor" ) DocumentParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentUpdate(id,body,securityContext);
    }
}