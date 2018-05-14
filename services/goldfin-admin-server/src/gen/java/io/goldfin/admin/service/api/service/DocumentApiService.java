package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.DocumentParameters;
import java.io.File;
import io.goldfin.admin.service.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class DocumentApiService {
    public abstract Response documentCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail,String description,Boolean process,SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentProcess(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentShowAll(SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentShowContent(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response documentUpdate(String id,DocumentParameters body,SecurityContext securityContext) throws NotFoundException;
}
