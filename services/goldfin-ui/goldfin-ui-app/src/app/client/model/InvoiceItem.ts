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
 * Common fields for all invoice line items
 */
export interface InvoiceItem {
    /**
     * Invoice item ID if specified
     */
    itemId?: string;

    /**
     * Inventory resource ID
     */
    resourceId?: string;

    /**
     * Invoice item description
     */
    description?: string;

    /**
     * Cost per unit
     */
    unitAmount?: number;

    /**
     * Number of units
     */
    units?: number;

    /**
     * Total cost for all units
     */
    totalAmount?: number;

    /**
     * Item currency
     */
    currency?: string;

    /**
     * Begining of the time range
     */
    startDate?: Date;

    /**
     * End of the time range
     */
    endDate?: Date;

    /**
     * If true, this is a one-time charge and the starting date provides the date
     */
    oneTimeCharge?: boolean;

    region?: models.DocumentRegion;

    /**
     * Id of an inventory description
     */
    inventoryId?: string;

    inventoryType?: InvoiceItem.InventoryTypeEnum;

}
export namespace InvoiceItem {
    export enum InventoryTypeEnum {
        HOST = <any> 'HOST'
    }
}
