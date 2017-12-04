package io.goldfin.service.admin.api.service;

import io.goldfin.service.admin.api.service.*;
import io.goldfin.service.admin.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.service.admin.api.model.ModelApiResponse;
import io.goldfin.service.admin.api.model.Tenant;
import io.goldfin.service.admin.api.model.TenantParameters;

import java.util.List;
import io.goldfin.service.admin.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class TenantApiService {
    public abstract Response tenantCreate(TenantParameters body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response tenantDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response tenantShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response tenantShowall(SecurityContext securityContext) throws NotFoundException;
    public abstract Response tenantUpdate(String id,TenantParameters body,SecurityContext securityContext) throws NotFoundException;
}
