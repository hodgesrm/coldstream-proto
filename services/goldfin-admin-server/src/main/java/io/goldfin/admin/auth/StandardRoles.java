//  ========================================================================
//  Copyright (c) 2018 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.admin.auth;

/**
 * Defines current roles.
 */
public enum StandardRoles {
	/** A system administrator who can manage the entire service. */
	SUPERUSER,
	/** A tenant user. */
	USER
}