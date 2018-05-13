package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.admin.service.api.model.Host;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class HostApiService {
    public abstract Response hostDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response hostShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response hostShowAll(SecurityContext securityContext) throws NotFoundException;
}
