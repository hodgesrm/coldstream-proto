/*
 * Copyright (c) 2016 VMware, Inc. All Rights Reserved.
 * This software is released under MIT license.
 * The full license information can be found in LICENSE in the root directory of this project.
 */
import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';

import { AboutComponent } from './about/about.component';
import { HomeComponent } from './home/home.component';
import { SourcesComponent } from './documents/sources.component';
import { DocumentsComponent } from './documents/documents.component';
import { InvoicesComponent } from './invoices/invoices.component';
import { InventoryComponent } from './inventory/inventory.component';
import { PricesComponent } from './prices/prices.component';
import { VendorsComponent } from './vendors/vendors.component';
import { LoginComponent } from './login/login.component';
import { AuthGuardService } from './services/auth.guard.service';

export const ROUTES: Routes = [
    {path: '', redirectTo: '/login', pathMatch: 'full'},
    {path: 'login', component: LoginComponent}, 
    {
      path: 'home', component: HomeComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'sources', component: SourcesComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'invoices', component: InvoicesComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'inventory', component: InventoryComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'prices', component: PricesComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'vendors', component: VendorsComponent,
      canActivate: [AuthGuardService]
    },
    {
      path: 'about', component: AboutComponent,
      canActivate: [AuthGuardService]
    },
    {path: '**', redirectTo: '/login', pathMatch: 'full'},
];

export const ROUTING: ModuleWithProviders = RouterModule.forRoot(ROUTES);
