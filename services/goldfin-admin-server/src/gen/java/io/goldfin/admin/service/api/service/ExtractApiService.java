package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class ExtractApiService {
    public abstract Response extractDownload( @NotNull String extractType, String filter, Integer limit, String order, String output,SecurityContext securityContext) throws NotFoundException;
    public abstract Response extractTypes(SecurityContext securityContext) throws NotFoundException;
}
