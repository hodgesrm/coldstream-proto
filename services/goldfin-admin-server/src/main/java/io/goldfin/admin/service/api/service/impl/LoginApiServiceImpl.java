/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.restapi.jetty.SecurityAuthenticator;
import io.goldfin.admin.service.api.model.LoginCredentials;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.LoginApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/**
 * Implements user authentication.
 */
public class LoginApiServiceImpl extends LoginApiService {
	static final Logger logger = LoggerFactory.getLogger(LoginApiServiceImpl.class);

	@Override
	public Response loginByCredentials(LoginCredentials body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			String token = um.login(body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK"))
					.header(SecurityAuthenticator.API_KEY_HEADER, token).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}
}