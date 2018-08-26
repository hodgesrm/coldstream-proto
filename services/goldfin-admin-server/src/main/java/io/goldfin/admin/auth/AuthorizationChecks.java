//  ========================================================================
//  Copyright (c) 2018 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.admin.auth;

import javax.ws.rs.core.SecurityContext;

import io.goldfin.admin.exceptions.UnauthorizedException;

/**
 * Implements authorization checks. To simplify client calls these can work
 * directly off a SecurityContext.
 */
public class AuthorizationChecks {
	/**
	 * Returns true if principal is allowed to create and delete tenants.
	 */
	public static boolean canManageAnyTenant(SecurityContext context) {
		return hasRole(getPrincipal(context), StandardRoles.SUPERUSER);
	}

	/**
	 * Ensures principal is allowed to create and delete tenants.
	 * 
	 * @throws UnauthorizedException
	 *             Thrown if principal cannot manage tenants
	 */
	public static void assertCanManageAnyTenant(SecurityContext context) {
		if (!canManageAnyTenant(context)) {
			throw new UnauthorizedException();
		}
	}

	/**
	 * Returns true if principal is allowed to create and delete users other than
	 * self.
	 */
	public static boolean canManageAnyUser(SecurityContext context) {
		return hasRole(getPrincipal(context), StandardRoles.SUPERUSER);
	}

	/**
	 * Ensures principal is allowed to create and delete users other than self.
	 * 
	 * @throws UnauthorizedException
	 *             Thrown if principal cannot manage tenants
	 */
	public static void assertCanManageAnyUser(SecurityContext context) {
		if (!canManageAnyUser(context)) {
			throw new UnauthorizedException();
		}
	}

	/**
	 * Returns true if principal is allowed to manage a specific user. This covers
	 * the case where a user makes changes to his/her own account.
	 */
	public static boolean canManageThisUser(SecurityContext context, String userId) {
		if (canManageAnyUser(context)) {
			return true;
		} else if (getPrincipal(context).getUserId().equals(userId)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Ensures principal is allowed to manage a specific user.
	 * 
	 * @throws UnauthorizedException
	 *             Thrown if principal cannot manage tenants
	 */
	public static void assertCanManageThisUser(SecurityContext context, String userId) {
		if (!canManageThisUser(context, userId)) {
			throw new UnauthorizedException();
		}
	}

	private static boolean hasRole(TenantUserPrincipal principal, StandardRoles role) {
		return role.toString().equals(principal.getRole());
	}

	public static TenantUserPrincipal getPrincipal(SecurityContext securityContext) {
		return (TenantUserPrincipal) securityContext.getUserPrincipal();
	}
}