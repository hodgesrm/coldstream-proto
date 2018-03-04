/**
 * Goldfin Service Admin API
 * REST API for Goldfin Service Administration
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@goldfin.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

import * as models from './models';

/**
 * User description
 */
export interface User {
    /**
     * Unique user id
     */
    id?: string;

    /**
     * Tenant ID
     */
    tenantId?: string;

    /**
     * Unique user name
     */
    username?: string;

    /**
     * Comma-separated list of user roles
     */
    roles?: string;

    /**
     * Date user record was created
     */
    creationDate?: string;

}
