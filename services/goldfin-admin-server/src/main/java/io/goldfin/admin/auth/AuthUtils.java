//  ========================================================================
//  Copyright (c) 2018 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.admin.auth;

import javax.ws.rs.core.SecurityContext;

/**
 * Convenience methods for operations related to authentication and
 * authorization.
 */
public class AuthUtils {
	public static TenantUserPrincipal getPrincipal(SecurityContext securityContext) {
		return (TenantUserPrincipal) securityContext.getUserPrincipal();
	}
}