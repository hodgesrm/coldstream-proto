/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import io.goldfin.admin.service.api.service.ApiResponseMessage;

/**
 * Helper methods for processing exceptions.
 */
public class ExceptionHelper {
	private final Logger logger;

	public ExceptionHelper(Logger logger) {
		this.logger = logger;
	}

	/** Convert exception to an HTTP response. */
	public Response toApiResponse(Exception e) {
		if (e instanceof EntityNotFoundException) {
			return clientErrorException(Status.NOT_FOUND, e);
		} else if (e instanceof InvalidInputException) {
			return clientErrorException(Status.BAD_REQUEST, e);
		} else if (e instanceof UnauthorizedException) {
			return clientErrorException(Status.UNAUTHORIZED, e);
		} else {
			logger.error("Unexpected error", e);
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	private Response clientErrorException(Status status, Exception e) {
		if (logger.isDebugEnabled()) {
			logger.debug(status.toString(), e);
		}
		return Response.status(status).entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
	}
}