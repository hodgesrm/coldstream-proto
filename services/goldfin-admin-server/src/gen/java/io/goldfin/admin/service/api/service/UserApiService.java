package io.goldfin.admin.service.api.service;

import io.goldfin.admin.service.api.service.*;
import io.goldfin.admin.service.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.admin.service.api.model.ModelApiResponse;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.admin.service.api.model.UserPasswordParameters;

import java.util.List;
import io.goldfin.admin.service.api.service.NotFoundException;

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
    public abstract Response userUpdatePassword(String id,UserPasswordParameters body,SecurityContext securityContext) throws NotFoundException;
}
