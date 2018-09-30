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
 * A set of one or more observations for analysis
 */
export interface DataSeries {
    /**
     * Series ID
     */
    id?: string;
    /**
     * Name of the source file if known
     */
    name?: string;
    /**
     * Optional description of the series
     */
    description?: string;
    /**
     * Internet media type (e.g., application/json)
     */
    contentType?: string;
    /**
     * Content length in bytes
     */
    contentLength?: number;
    /**
     * SHA-256 thumbprint of content
     */
    thumbprint?: string;
    /**
     * Storage locator for content
     */
    locator?: string;
    /**
     * The current processing state of the content.  
     */
    state?: DataSeries.StateEnum;
    /**
     * Kind of data series, e.g., observation
     */
    format?: DataSeries.FormatEnum;
    tags?: TagSet;
    /**
     * Date data was uploaded
     */
    creationDate?: string;
}
export namespace DataSeries {
    export type StateEnum = 'CREATED' | 'PROCESS_REQUESTED' | 'PROCESSED' | 'ERROR';
    export const StateEnum = {
        CREATED: 'CREATED' as StateEnum,
        PROCESSREQUESTED: 'PROCESS_REQUESTED' as StateEnum,
        PROCESSED: 'PROCESSED' as StateEnum,
        ERROR: 'ERROR' as StateEnum
    }
    export type FormatEnum = 'OBSERVATION' | 'UNKNOWN';
    export const FormatEnum = {
        OBSERVATION: 'OBSERVATION' as FormatEnum,
        UNKNOWN: 'UNKNOWN' as FormatEnum
    }
}