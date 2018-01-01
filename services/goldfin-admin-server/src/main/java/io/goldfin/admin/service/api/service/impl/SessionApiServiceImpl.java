package io.goldfin.admin.service.api.service.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.restapi.jetty.SecurityAuthenticator;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.SessionApiService;

/**
 * Implements session management specifically login and logout.
 */
public class SessionApiServiceImpl extends SessionApiService {
	static private final Logger logger = LoggerFactory.getLogger(SessionApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response loginByCredentials(LoginCredentials body, SecurityContext securityContext) {
		try {
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			String token = um.login(body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK"))
					.header(SecurityAuthenticator.API_KEY_HEADER, token).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response logout(String token, SecurityContext securityContext) {
		try {
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			um.logout(token);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK"))
					.header(SecurityAuthenticator.API_KEY_HEADER, token).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}
}
