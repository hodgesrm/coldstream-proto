package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.UserManager;
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
	@Override
	public Response userCreate(UserParameters body, SecurityContext securityContext) throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			tm.createUser(body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response userDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			tm.deleteUser(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response userShow(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			User user = tm.getUser(id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(user).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response userShowall(SecurityContext securityContext) throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			List<User> users = tm.getAllUsers();
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).entity(users).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response userUpdate(String id, UserParameters body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			tm.updateUser(id, body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}

	@Override
	public Response userUpdatePassword(String id, UserPasswordParameters body, SecurityContext securityContext)
			throws NotFoundException {
		try {
			UserManager tm = ManagerRegistry.getInstance().getManager(UserManager.class);
			tm.updateUserPassword(id, body);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage()))
					.build();
		}
	}
}
