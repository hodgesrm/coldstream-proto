package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.model.*;
import io.goldfin.front.invoice.api.service.InvoiceApiService;
import io.goldfin.front.invoice.api.service.factories.InvoiceApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.io.File;
import io.goldfin.front.invoice.api.model.InvoiceEnvelope;
import io.goldfin.front.invoice.api.model.InvoiceEnvelopeParameters;
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

@Path("/invoice")


@io.swagger.annotations.Api(description = "the invoice API")

public class InvoiceApi  {
   private final InvoiceApiService delegate;

   public InvoiceApi(@Context ServletConfig servletContext) {
      InvoiceApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("InvoiceApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (InvoiceApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = InvoiceApiServiceFactory.getInvoiceApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new invoice for logged in tenant", notes = "Upload a new invoice and kick off processing", response = InvoiceEnvelope.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created", response = InvoiceEnvelope.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invoice creation failed", response = InvoiceEnvelope.class) })
    public Response invoiceCreate(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
,@ApiParam(value = "A optional description of the invoice")@FormDataParam("description")  String description
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceCreate(fileInputStream, fileDetail,description,securityContext);
    }
    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete an invoice", notes = "Delete a single", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response invoiceDelete(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceDelete(id,securityContext);
    }
    @POST
    @Path("/{id}/process")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Start invoice processing", notes = "Run background OCR and interpretation on invoice.  The invoice state will be set to CREATED before this call returns.", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response invoiceProcess(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceProcess(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json", "application/xml", "text/xml" })
    @io.swagger.annotations.ApiOperation(value = "Show a single invoice", notes = "Return all information relative to a single invoice", response = InvoiceEnvelope.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = InvoiceEnvelope.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = InvoiceEnvelope.class) })
    public Response invoiceShow(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceShow(id,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json", "application/xml", "text/xml" })
    @io.swagger.annotations.ApiOperation(value = "List invoices", notes = "Return a list of all invoices", response = InvoiceEnvelope.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = InvoiceEnvelope.class, responseContainer = "List") })
    public Response invoiceShowAll(@ApiParam(value = "If true return complete invoice content, otherwise just the Invoice fields", defaultValue="true") @DefaultValue("true") @QueryParam("summary") Boolean summary
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceShowAll(summary,securityContext);
    }
    @PUT
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update an invoice", notes = "Update invoice description and tags. Changes to other fields are ignored", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response invoiceUpdate(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@ApiParam(value = "Invoice descriptor" ) InvoiceEnvelopeParameters body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceUpdate(id,body,securityContext);
    }
    @POST
    @Path("/{id}/validate")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Start invoice validations", notes = "Run invoice validations", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response invoiceValidate(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceValidate(id,securityContext);
    }
}
