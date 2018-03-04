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
 * Location within a document page expressed in page number and pixel region coordinates on the page
 */
export interface DocumentRegion {
    /**
     * Document ID
     */
    documentId?: string;

    /**
     * Page number
     */
    page?: number;

    /**
     * Left pixel coordinate
     */
    left?: number;

    /**
     * Top pixel coordinate
     */
    top?: number;

    /**
     * Right pixel coordinate
     */
    right?: number;

    /**
     * Bottom pixel coordinate
     */
    bottom?: number;

}
