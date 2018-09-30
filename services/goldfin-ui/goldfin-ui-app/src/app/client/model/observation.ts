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
import { TagSet } from './tagSet';


/**
 * Generic observation data related to a vendor offering
 */
export interface Observation {
    /**
     * Vendor identifier key
     */
    vendorIdentifier?: string;
    /**
     * Effective date when observation was made
     */
    effectiveDate?: Date;
    /**
     * A randomly generated integer to help ensure loading is idempotent (i.e., can be repeated without generating multiple copies of the same observation)
     */
    nonce?: number;
    /**
     * Optional description of observation
     */
    description?: string;
    /**
     * Kind of observation, e.g., host inventory.
     */
    observationType?: Observation.ObservationTypeEnum;
    /**
     * String containing serialized observation data
     */
    data?: string;
    /**
     * Data format version
     */
    version?: string;
    tags?: TagSet;
}
export namespace Observation {
    export type ObservationTypeEnum = 'HOST_INVENTORY';
    export const ObservationTypeEnum = {
        INVENTORY: 'HOST_INVENTORY' as ObservationTypeEnum
    }
}