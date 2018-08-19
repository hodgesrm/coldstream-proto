/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.http.entity.ContentType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.managers.DataSeriesManager;
import io.goldfin.admin.managers.DocumentManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.DocumentParameters;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.DataApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/**
 * Implements REST operations on data series.
 */
public class DataApiServiceImpl extends DataApiService {
	static private final Logger logger = LoggerFactory.getLogger(DataApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response dataCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail, String description,
			String tags, Boolean process, SecurityContext securityContext) throws NotFoundException {
		try {
			// Infer content type since we just see 'form-data' from Jax RS implementation.
			File file = new File(fileDetail.getFileName());
			ContentType contentType = null;
			if (file.getName().toLowerCase().endsWith(".json")) {
				contentType = ContentType.APPLICATION_JSON;
			} else if (file.getName().toLowerCase().endsWith(".zip")) {
				contentType = ContentType.create("application/zip");
			} else {
				throw new InvalidInputException(
						String.format("File must have .json or .zip suffix: %s", file.getAbsolutePath()));
			}
			// Store file.
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			DataSeries dm = dsm.createDataSeries(securityContext.getUserPrincipal(), fileInputStream,
					fileDetail.getFileName(), description, tags, contentType.toString(), process);
			return Response.ok().entity(dm).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response dataDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			dsm.deleteDataSeries(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response dataShowContent(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response dataProcess(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			dsm.processDataSeries(securityContext.getUserPrincipal(), id);
			return Response.accepted().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response dataShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			Principal principal = securityContext.getUserPrincipal();
			DataSeries ds = dsm.getDataSeries(principal, id);
			return Response.ok().entity(ds).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response dataShowAll(SecurityContext securityContext) throws NotFoundException {
		try {
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			List<DataSeries> series = dsm.getAllDataSeries(securityContext.getUserPrincipal());
			return Response.ok().entity(series).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response dataUpdate(String id, DataSeries body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			DataSeriesManager dsm = ManagerRegistry.getInstance().getManager(DataSeriesManager.class);
			dsm.updateDataSeries(securityContext.getUserPrincipal(), id, body);
			return Response.accepted().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}
}
