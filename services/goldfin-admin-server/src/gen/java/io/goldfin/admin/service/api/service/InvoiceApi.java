package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.model.*;
import io.goldfin.admin.service.api.service.InvoiceApiService;
import io.goldfin.admin.service.api.service.factories.InvoiceApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.io.File;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceValidationResult;

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

    @DELETE
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete an invoice", notes = "Delete an invoice.  It can be recreated by rescanning the corresponding document", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = void.class) })
    public Response invoiceDelete(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceDelete(id,securityContext);
    }
    @GET
    @Path("/{id}/download")
    
    @Produces({ "application/pdf", "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Download invoice document", notes = "Download invoice document", response = File.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = File.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = File.class) })
    public Response invoiceDownload(@ApiParam(value = "Document ID",required=true) @PathParam("id") String id
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceDownload(id,securityContext);
    }
    @GET
    @Path("/{id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Show a single invoice", notes = "Return all information relative to a single invoice", response = Invoice.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = Invoice.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = Invoice.class) })
    public Response invoiceShow(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@ApiParam(value = "If true, return full invoices with all line items") @QueryParam("full") Boolean full
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceShow(id,full,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "List invoices", notes = "Return a list of all invoices", response = Invoice.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful query", response = Invoice.class, responseContainer = "List") })
    public Response invoiceShowAll(@ApiParam(value = "If true, return full invoices with all line items") @QueryParam("full") Boolean full
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceShowAll(full,securityContext);
    }
    @POST
    @Path("/{id}/validate")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Start invoice validations", notes = "Run invoice validations", response = InvoiceValidationResult.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "ApiKey"),
        @io.swagger.annotations.Authorization(value = "SessionKey")
    }, tags={ "invoice", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = InvoiceValidationResult.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found", response = InvoiceValidationResult.class, responseContainer = "List") })
    public Response invoiceValidate(@ApiParam(value = "Invoice ID",required=true) @PathParam("id") String id
,@ApiParam(value = "If true, return only failing checks. Otherwise return all results including checks that succeed", defaultValue="false") @DefaultValue("false") @QueryParam("onlyFailing") Boolean onlyFailing
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.invoiceValidate(id,onlyFailing,securityContext);
    }
}
