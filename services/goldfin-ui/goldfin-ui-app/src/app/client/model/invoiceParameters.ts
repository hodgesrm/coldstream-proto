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
 * User settable invoice parameters
 */
export interface InvoiceParameters {
    /**
     * A user-provided description of the invoice
     */
    description?: string;
    tags?: TagSet;
}