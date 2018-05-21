/**
 * Goldfin Service API
 * REST API for Goldfin Intelligent Invoice Processing
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
 * Parameters to change a user password
 */
export interface UserPasswordParameters {
    /**
     * Existing user password.  Required to change user password.
     */
    oldPassword?: string;

    /**
     * New user password.
     */
    newPassword?: string;

}
