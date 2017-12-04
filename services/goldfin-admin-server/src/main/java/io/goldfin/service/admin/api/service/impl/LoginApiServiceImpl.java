/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.service.admin.api.service.impl;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.restapi.jetty.SecurityAuthenticator;
import io.goldfin.service.admin.api.model.LoginCredentials;
import io.goldfin.service.admin.api.service.ApiResponseMessage;
import io.goldfin.service.admin.api.service.LoginApiService;
import io.goldfin.service.admin.api.service.NotFoundException;

/**
 * Implements user authentication.
 */
public class LoginApiServiceImpl extends LoginApiService {
	static final Logger logger = LoggerFactory.getLogger(LoginApiServiceImpl.class);

	@Override
	public Response loginByCredentials(LoginCredentials body, SecurityContext securityContext)
			throws NotFoundException {
		logger.info("User logged in: " + body.getUser());
		String apiKey = UUID.randomUUID().toString();
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK"))
				.header(SecurityAuthenticator.API_KEY_HEADER, apiKey).build();
	}
}