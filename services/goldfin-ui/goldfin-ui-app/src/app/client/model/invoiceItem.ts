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
import { DocumentRegion } from './documentRegion';
import { TagSet } from './tagSet';


/**
 * Common fields for all invoice line items
 */
export interface InvoiceItem {
    /**
     * Row number of line item starting at 1.
     */
    rid?: number;
    /**
     * Row ID of a summary invoice item to which this item belongs
     */
    parentRid?: number;
    /**
     * Type of invoice row item  * DETAIL - A payable item  * SUMMARY - A sub-total or total line 
     */
    itemRowType?: InvoiceItem.ItemRowTypeEnum;
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
     * Type of multiplier for computing invoice item total  * MONTH - Monthly subscription (may be prorated depending on vendor)  * HOUR - Hours in use  * USER - Number of users  * GB - Gigabytes (for example as a unit of storage or transfer)  * OTHER - Some other unit of consumption 
     */
    unitType?: InvoiceItem.UnitTypeEnum;
    /**
     * Item cost for all units without credits or taxes
     */
    subtotalAmount?: number;
    /**
     * Credit applied to line item
     */
    credit?: number;
    /**
     * Tax on line item
     */
    tax?: number;
    /**
     * Total cost for all units including taxes and credits
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
     * Type of charge  * RECURRING - Recurs every interval e.g. monthly  * ONE_TIME - A one-time charge delivered on starting date 
     */
    chargeType?: InvoiceItem.ChargeTypeEnum;
    region?: DocumentRegion;
    /**
     * Id of an inventory description
     */
    inventoryId?: string;
    inventoryType?: InvoiceItem.InventoryTypeEnum;
    tags?: TagSet;
}
export namespace InvoiceItem {
    export type ItemRowTypeEnum = 'DETAIL' | 'SUMMARY';
    export const ItemRowTypeEnum = {
        DETAIL: 'DETAIL' as ItemRowTypeEnum,
        SUMMARY: 'SUMMARY' as ItemRowTypeEnum
    }
    export type UnitTypeEnum = 'MONTH' | 'HOUR' | 'USER' | 'GB' | 'OTHER';
    export const UnitTypeEnum = {
        MONTH: 'MONTH' as UnitTypeEnum,
        HOUR: 'HOUR' as UnitTypeEnum,
        USER: 'USER' as UnitTypeEnum,
        GB: 'GB' as UnitTypeEnum,
        OTHER: 'OTHER' as UnitTypeEnum
    }
    export type ChargeTypeEnum = 'RECURRING' | 'ONE-TIME';
    export const ChargeTypeEnum = {
        RECURRING: 'RECURRING' as ChargeTypeEnum,
        ONETIME: 'ONE-TIME' as ChargeTypeEnum
    }
    export type InventoryTypeEnum = 'HOST';
    export const InventoryTypeEnum = {
        HOST: 'HOST' as InventoryTypeEnum
    }
}
