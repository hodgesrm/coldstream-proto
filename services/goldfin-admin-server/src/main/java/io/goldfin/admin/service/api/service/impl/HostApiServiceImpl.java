/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.HostManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.HostApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/**
 * Implements operations on hosts.
 */
public class HostApiServiceImpl extends HostApiService {
	static private final Logger logger = LoggerFactory.getLogger(HostApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response hostDelete(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response hostShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			HostManager hm = ManagerRegistry.getInstance().getManager(HostManager.class);
			Host host = hm.getHost(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(host).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response hostShowAll(SecurityContext securityContext) throws NotFoundException {
		try {
			HostManager hm = ManagerRegistry.getInstance().getManager(HostManager.class);
			List<Host> hosts = hm.getAllHosts(securityContext.getUserPrincipal());
			return Response.ok().entity(hosts).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}
}
