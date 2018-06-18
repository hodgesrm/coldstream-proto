export * from './DocumentApi';
import { DocumentApi } from './DocumentApi';
export * from './ExtractApi';
import { ExtractApi } from './ExtractApi';
export * from './InventoryApi';
import { InventoryApi } from './InventoryApi';
export * from './InvoiceApi';
import { InvoiceApi } from './InvoiceApi';
export * from './SecurityApi';
import { SecurityApi } from './SecurityApi';
export * from './TenantApi';
import { TenantApi } from './TenantApi';
export * from './VendorApi';
import { VendorApi } from './VendorApi';
export const APIS = [DocumentApi, ExtractApi, InventoryApi, InvoiceApi, SecurityApi, TenantApi, VendorApi];
