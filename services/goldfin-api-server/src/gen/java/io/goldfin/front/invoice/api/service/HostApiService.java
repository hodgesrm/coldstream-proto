package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.service.*;
import io.goldfin.front.invoice.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.front.invoice.api.model.Host;
import io.goldfin.front.invoice.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class HostApiService {
    public abstract Response inventoryHostCreate(Host body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response inventoryHostDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response inventoryHostShowAll( Boolean summary,SecurityContext securityContext) throws NotFoundException;
    public abstract Response invoiceHostShow(String id,SecurityContext securityContext) throws NotFoundException;
}
