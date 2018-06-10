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
 * Host record from inventory
 */
export interface Host {
    /**
     * Host record ID
     */
    id?: string;

    /**
     * Internal host ID from vendor used to identify host in inventory
     */
    hostId?: string;

    /**
     * Inventory resource ID that can be related to invoice item resource ID
     */
    resourceId?: string;

    /**
     * Observation effective date
     */
    effectiveDate?: Date;

    /**
     * Vendor identifier key
     */
    vendorIdentifier?: string;

    /**
     * Id of data series from which this host record was derived
     */
    dataSeriesId?: string;

    /**
     * Type of host
     */
    hostType?: Host.HostTypeEnum;

    /**
     * Host model or marketing name
     */
    hostModel?: string;

    /**
     * Geographic region
     */
    region?: string;

    /**
     * Availability zone
     */
    zone?: string;

    /**
     * Data center
     */
    datacenter?: string;

    /**
     * CPU model
     */
    cpu?: string;

    /**
     * Number of CPU sockets
     */
    socketCount?: number;

    /**
     * Number of cores per socket
     */
    coreCount?: number;

    /**
     * Number of hardware threads per core
     */
    threadCount?: number;

    /**
     * Size of RAM in bytes
     */
    ram?: number;

    /**
     * Size of HDD storage in bytes
     */
    hdd?: number;

    /**
     * Size of SSD storage in bytes
     */
    ssd?: number;

    /**
     * Number of network interface cards (NICs)
     */
    nicCount?: number;

    /**
     * Number of bytes of network traffer per billing period if there is a hard limit
     */
    networkTrafficLimit?: number;

    /**
     * If true backup is enabled for this host
     */
    backupEnabled?: boolean;

}
export namespace Host {
    export enum HostTypeEnum {
        BAREMETAL = <any> 'BARE_METAL',
        CLOUD = <any> 'CLOUD',
        UNKNOWN = <any> 'UNKNOWN'
    }
}