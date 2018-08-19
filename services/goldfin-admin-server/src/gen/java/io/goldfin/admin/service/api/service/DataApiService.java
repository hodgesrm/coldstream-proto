package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.admin.service.api.model.DataSeries;
import java.io.File;
import io.goldfin.admin.service.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class DataApiService {
    public abstract Response dataCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail,String description,String tags,Boolean process,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataProcess(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataShowAll(SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataShowContent(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response dataUpdate(String id,DataSeries body,SecurityContext securityContext) throws NotFoundException;
}
