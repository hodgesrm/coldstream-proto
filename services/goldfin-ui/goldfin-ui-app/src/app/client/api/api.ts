export * from './data.service';
import { DataService } from './data.service';
export * from './document.service';
import { DocumentService } from './document.service';
export * from './extract.service';
import { ExtractService } from './extract.service';
export * from './inventory.service';
import { InventoryService } from './inventory.service';
export * from './invoice.service';
import { InvoiceService } from './invoice.service';
export * from './security.service';
import { SecurityService } from './security.service';
export * from './tenant.service';
import { TenantService } from './tenant.service';
export * from './user.service';
import { UserService } from './user.service';
export * from './vendor.service';
import { VendorService } from './vendor.service';
export const APIS = [DataService, DocumentService, ExtractService, InventoryService, InvoiceService, SecurityService, TenantService, UserService, VendorService];
