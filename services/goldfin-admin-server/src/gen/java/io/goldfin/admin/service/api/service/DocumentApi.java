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
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = Document.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invoice creation failed", response = ModelApiResponse.class) })
    public Response documentCreate(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@ApiParam(value = "An optional description of the document")@FormDataParam("description")  String description
,@ApiParam(value = "Optional tags that apply to this document passed as a JSON string containing name-value pairs.")@FormDataParam("tags")  String tags
,@ApiParam(value = "Optional flag to kick off scanning automatically if true", defaultValue="true")@FormDataParam("process")  Boolean process
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentCreate(fileInputStream, fileDetail,description,tags,process,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete a invoice", notes = "Delete a single document and associated semantic content", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    public Response documentDelete(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentDelete(id,securityContext);
    }
    @GET
    @Path("/{id}/download")
    
    @Produces({ "application/pdf", "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Download content", notes = "Download document content", response = File.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    public Response documentDownload(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentDownload(id,securityContext);
    }
    @POST
    @Path("/{id}/process")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Kick off document analysis", notes = "Run background scanning on document.  The document state and semantic information will be updated when finished.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    public Response documentProcess(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentProcess(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Return document metadata", notes = "Download document metadata without content", response = Document.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Document.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    public Response documentShow(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List documents", notes = "Return a list of all documents", response = Document.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = Document.class, responseContainer = "List") })
    public Response documentShowAll(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentShowAll(securityContext);
    }
    @PUT
    @Path("/{id}")
    @Consumes({ "application/json" })
    
    @io.swagger.annotations.ApiOperation(value = "Update a document", notes = "Update document description and/or tags. Tags do not propagate to already scanned invoices but will apply to new ones.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "document", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    public Response documentUpdate(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Document parameters" ) DocumentParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.documentUpdate(id,body,securityContext);
    }
}
