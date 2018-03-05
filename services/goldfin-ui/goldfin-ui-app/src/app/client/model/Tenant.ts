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
 * Tenant description
 */
export interface Tenant {
    /**
     * Unique tenant id
     */
    id?: string;

    /**
     * Unique tenant name
     */
    name?: string;

    /**
     * Tenant schema suffix
     */
    schemaSuffix?: string;

    /**
     * Optional information about the tenant
     */
    description?: string;

    /**
     * The current processing state of the tenant. 
     */
    state?: Tenant.StateEnum;

    /**
     * Date invoice record was created
     */
    creationDate?: string;

}
export namespace Tenant {
    export enum StateEnum {
        PENDING = <any> 'PENDING',
        ENABLED = <any> 'ENABLED',
        DISABLED = <any> 'DISABLED'
    }
}