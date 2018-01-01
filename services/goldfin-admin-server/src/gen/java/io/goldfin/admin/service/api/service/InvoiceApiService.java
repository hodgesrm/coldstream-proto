package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelopeParameters;
import io.goldfin.admin.service.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class InvoiceApiService {
    public abstract Response invoiceCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail,String description,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceProcess(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceShowAll( Boolean summary,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceUpdate(String id,InvoiceEnvelopeParameters body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceValidate(String id,SecurityContext securityContext) throws NotFoundException;
}
