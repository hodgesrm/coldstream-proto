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
 * A key-value pair used to add relevant descriptive information. Tags are not interpreted and may contain data like business unit IDs, contract names, or other information users find relevant to classify or associate the data to which they apply.
 */
export interface Tag {
    /**
     * A user-defined name for the tag
     */
    name?: string;
    /**
     * A user-defined value for the tag
     */
    value?: string;
}