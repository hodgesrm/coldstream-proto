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


/**
 * Login credentials
 */
export interface LoginCredentials {
    /**
     * User name in form 'username@tenantname'
     */
    user?: string;
    /**
     * User password
     */
    password?: string;
}