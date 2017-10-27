package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.service.*;
import io.goldfin.front.invoice.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.front.invoice.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class ExportApiService {
    public abstract Response exportInvoice( String outputFormat, String timeSlice, String filterSpec,SecurityContext securityContext) throws NotFoundException;
}
