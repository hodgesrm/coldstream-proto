/*
 * Copyright (c) 2016 VMware, Inc. All Rights Reserved.
 * This software is released under MIT license.
 * The full license information can be found in LICENSE in the root directory of this project.
 */
import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';

import { AboutComponent } from './about/about.component';
import { HomeComponent } from './home/home.component';
import { DocumentsComponent } from './documents/documents.component';
import { InvoicesComponent } from './invoices/invoices.component';
import { InventoryComponent } from './inventory/inventory.component';
import { PricesComponent } from './prices/prices.component';
import { VendorsComponent } from './vendors/vendors.component';
import { LoginComponent } from './login/login.component';

export const ROUTES: Routes = [
    {path: '', redirectTo: '/login', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'documents', component: DocumentsComponent},
    {path: 'invoices', component: InvoicesComponent},
    {path: 'inventory', component: InventoryComponent},
    {path: 'prices', component: PricesComponent},
    {path: 'vendors', component: VendorsComponent},
    {path: 'about', component: AboutComponent},
    {path: 'login', component: LoginComponent}
];

export const ROUTING: ModuleWithProviders = RouterModule.forRoot(ROUTES);
