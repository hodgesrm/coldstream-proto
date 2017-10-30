package io.goldfin.front.invoice.api.service.impl;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.front.invoice.api.model.LoginRequest;
import io.goldfin.front.invoice.api.service.ApiResponseMessage;
import io.goldfin.front.invoice.api.service.LoginApiService;
import io.goldfin.front.invoice.api.service.NotFoundException;
import io.goldfin.invoice.restapi.jetty.SecurityAuthenticator;

/**
 * Implements user authentication.
 */
public class LoginApiServiceImpl extends LoginApiService {
	static final Logger logger = LoggerFactory.getLogger(LoginApiServiceImpl.class);

	@Override
	public Response loginByCredentials(LoginRequest body, SecurityContext securityContext) throws NotFoundException {
		logger.info("User logged in: " + body.getUser());
		String apiKey = UUID.randomUUID().toString();
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK"))
				.header(SecurityAuthenticator.API_KEY_HEADER, apiKey).build();
	}
}