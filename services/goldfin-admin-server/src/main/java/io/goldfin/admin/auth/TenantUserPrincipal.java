//  ========================================================================
//  Copyright (c) 2018 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.admin.auth;

import java.io.Serializable;
import java.security.Principal;

/**
 * Implements a tenant principal. This may be a normal tenant user or a
 * superuser acting on a specific tenant.
 */
public class TenantUserPrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 1L;

	private final String userId;
	private final String tenantId;
	private final String effectiveTenantId;
	private final String role;

	/**
	 * Construct a new tenant principal.
	 * 
	 * @param userId
	 *            Unique ID of the user
	 * @param tenantId
	 *            Tenant to which this user belongs
	 * @param effectiveTenantId
	 *            Tenant on which this user is acting. For normal tenant users this
	 *            is always the same as tenantId. For super users it can be the name
	 *            of a tenant on which the superuser is acting.
	 * @param role
	 *            Current role of the user
	 */
	public TenantUserPrincipal(String userId, String tenantId, String effectiveTenantId, String role) {
		this.userId = userId;
		this.tenantId = tenantId;
		this.effectiveTenantId = effectiveTenantId;
		this.role = role;
	}

	@Override
	public String getName() {
		return userId;
	}

	public String getUserId() {
		return userId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getEffectiveTenantId() {
		return effectiveTenantId;
	}

	public String getRole() {
		return role;
	}

	@Override
	public String toString() {
		return String.format("%s: userId=%s, tenantId=%s, effectiveTenantId=%s, role=%s", userId, tenantId,
				effectiveTenantId, role);
	}
}