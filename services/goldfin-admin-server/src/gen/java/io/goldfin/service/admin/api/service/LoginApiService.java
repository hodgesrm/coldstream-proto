package io.goldfin.service.admin.api.service;

import io.goldfin.service.admin.api.service.*;
import io.goldfin.service.admin.api.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.goldfin.service.admin.api.model.LoginCredentials;
import io.goldfin.service.admin.api.model.ModelApiResponse;

import java.util.List;
import io.goldfin.service.admin.api.service.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

public abstract class LoginApiService {
    public abstract Response loginByCredentials(LoginCredentials body,SecurityContext securityContext) throws NotFoundException;
}
