/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.managers.ExtractManager;
import io.goldfin.admin.managers.ExtractManager.OutputTypes;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.ExtractApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/** Handle requests for extracts. */
public class ExtractApiServiceImpl extends ExtractApiService {
	static private final Logger logger = LoggerFactory.getLogger(ExtractApiServiceImpl.class);

	private static MediaType TEXT_CSV = new MediaType("text", "csv");

	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override

	public Response extractDownload(String extractType, String filter, Integer limit, String order, String output,
			SecurityContext securityContext) throws NotFoundException {
		try {
			// Limit must be converted to proper int to avoid null pointer exceptions. 
			int limitAsInt = (limit == null) ? 0 : limit.intValue();
			ExtractManager em = ManagerRegistry.getInstance().getManager(ExtractManager.class);
			if ("csv".equals(output.toLowerCase())) {
				String csv = em.getExtract(securityContext.getUserPrincipal(), extractType, filter, limitAsInt, order, OutputTypes.CSV);
				Response.ResponseBuilder builder = Response.ok(csv, TEXT_CSV);
				builder.header("Content-Disposition", "inline; filename=extract.csv");
				return builder.build();
			} else {
				throw new InvalidInputException("Unrecogized output type: " + output);
			}
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response extractTypes(SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}