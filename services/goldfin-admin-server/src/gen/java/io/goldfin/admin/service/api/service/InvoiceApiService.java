package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceValidationResult;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class InvoiceApiService {
    public abstract Response invoiceDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceDownload(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceShow(String id, Boolean full,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceShowAll( Boolean full,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceValidate(String id, Boolean onlyFailing,SecurityContext securityContext) throws NotFoundException;
}
