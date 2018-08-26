/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.auth.AuthorizationChecks;
import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.service.api.model.ApiKey;
import io.goldfin.admin.service.api.model.ApiKeyParameters;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.admin.service.api.model.UserParameters;
import io.goldfin.admin.service.api.model.UserPasswordParameters;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.NotFoundException;
import io.goldfin.admin.service.api.service.UserApiService;

/**
 * Perform operations on users.
 */
public class UserApiServiceImpl extends UserApiService {
	static private final Logger logger = LoggerFactory.getLogger(UserApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response userCreate(UserParameters body, SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyUser(securityContext);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			User user = um.createUser(body);
			return Response.ok().entity(user).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response userDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyUser(securityContext);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			um.deleteUser(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response userShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			String userId = convertToRealUserId(id, securityContext);
			AuthorizationChecks.assertCanManageThisUser(securityContext, userId);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			User user = um.getUser(userId);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(user).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response userShowall(SecurityContext securityContext) throws NotFoundException {
		try {
			AuthorizationChecks.assertCanManageAnyUser(securityContext);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			List<User> users = um.getAllUsers();
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(users).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response userUpdate(String id, UserParameters body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			String userId = convertToRealUserId(id, securityContext);
			AuthorizationChecks.assertCanManageThisUser(securityContext, userId);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			um.updateUser(userId, body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response userUpdatePassword(String id, UserPasswordParameters body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			String userId = convertToRealUserId(id, securityContext);
			AuthorizationChecks.assertCanManageThisUser(securityContext, userId);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			um.updateUserPassword(userId, body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response apikeyCreate(String userId, ApiKeyParameters body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			userId = convertToRealUserId(userId, securityContext);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			ApiKey apiKey = um.createApiKey(userId, body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(apiKey).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response apikeyDelete(String userId, String keyId, SecurityContext securityContext)
			throws NotFoundException {
		try {
			userId = convertToRealUserId(userId, securityContext);
			AuthorizationChecks.assertCanManageThisUser(securityContext, userId);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			um.deleteApiKey(userId, keyId);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response apikeyShowAll(String userId, SecurityContext securityContext) throws NotFoundException {
		try {
			userId = convertToRealUserId(userId, securityContext);
			AuthorizationChecks.assertCanManageThisUser(securityContext, userId);
			UserManager um = ManagerRegistry.getInstance().getManager(UserManager.class);
			List<ApiKey> apiKeys = um.getAllApiKeys(userId);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(apiKeys).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	// Convert current user to actual ID.
	private String convertToRealUserId(String userId, SecurityContext context) {
		if (UserManager.CURRENT_USER_ID.equals(userId)) {
			return context.getUserPrincipal().getName();
		} else {
			return userId;
		}
	}
}