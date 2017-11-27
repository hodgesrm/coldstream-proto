package io.goldfin.front.invoice.api.service;

import io.goldfin.front.invoice.api.service.*;
import io.goldfin.front.invoice.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.front.invoice.api.model.ModelApiResponse;
import io.goldfin.front.invoice.api.model.Vendor;
import io.goldfin.front.invoice.api.model.VendorParameters;

import java.util.List;
import io.goldfin.front.invoice.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class VendorApiService {
    public abstract Response vendorCreate(VendorParameters body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response vendorDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response vendorShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response vendorShowall(SecurityContext securityContext) throws NotFoundException;
    public abstract Response vendorUpdate(String id,VendorParameters body,SecurityContext securityContext) throws NotFoundException;
}
