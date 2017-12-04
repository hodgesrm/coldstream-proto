package io.goldfin.service.admin.api.service;

import io.goldfin.service.admin.api.service.*;
import io.goldfin.service.admin.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.service.admin.api.model.ModelApiResponse;
import io.goldfin.service.admin.api.model.Tenant;
import io.goldfin.service.admin.api.model.User;
import io.goldfin.service.admin.api.model.UserParameters;

import java.util.List;
import io.goldfin.service.admin.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class UserApiService {
    public abstract Response userCreate(UserParameters body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response userDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response userShow(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response userShowall(SecurityContext securityContext) throws NotFoundException;
    public abstract Response userUpdate(String id,UserParameters body,SecurityContext securityContext) throws NotFoundException;
}
