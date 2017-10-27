package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.model.*;
import io.goldfin.front.invoice.api.service.ExportApiService;
import io.goldfin.front.invoice.api.service.factories.ExportApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

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

@Path("/export")


@io.swagger.annotations.Api(description = "the export API")

public class ExportApi  {
   private final ExportApiService delegate;

   public ExportApi(@Context ServletConfig servletContext) {
      ExportApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("ExportApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (ExportApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = ExportApiServiceFactory.getExportApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/invoice")
    
    @Produces({ "text/plain", "application/octet-stream" })
    @io.swagger.annotations.ApiOperation(value = "Export invoice data", notes = "Export selected invoice data to useful formats", response = void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "UserSecurity")
    }, tags={ "export", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid request", response = void.class) })
    public Response exportInvoice(@ApiParam(value = "Output format to generate.  CSV is the only currently allowed value.") @QueryParam("outputFormat") String outputFormat
,@ApiParam(value = "Optional field to explode values out by a time unit.  Allowed values are DAY, HOUR, MINUTE") @QueryParam("timeSlice") String timeSlice
,@ApiParam(value = "Predicate list to select invoices.") @QueryParam("filterSpec") String filterSpec
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.exportInvoice(outputFormat,timeSlice,filterSpec,securityContext);
    }
}
