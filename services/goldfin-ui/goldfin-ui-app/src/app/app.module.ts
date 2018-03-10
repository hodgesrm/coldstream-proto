import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { ClarityModule } from 'clarity-angular';
import { AppComponent } from './app.component';
import { ROUTING } from "./app.routing";
import { HomeComponent } from "./home/home.component";
import { AssetSpendComponent } from "./home/asset-spend.component";
import { IndustryBaselineComponent } from "./home/industry-baseline.component";
import { LoginComponent } from "./login/login.component";
import { InvoicesComponent } from "./invoices/invoices.component";
import { DocumentsComponent } from "./documents/documents.component";
import { InventoryComponent } from "./inventory/inventory.component";
import { InventoryHostsComponent } from "./inventory/inventory_hosts.component";
import { PricesComponent } from "./prices/prices.component";
import { PricesHostsComponent } from "./prices/prices_hosts.component";
import { VendorsComponent } from "./vendors/vendors.component";
import { ErrorModalComponent } from "./utility/error-modal.component";
import { AboutComponent } from "./about/about.component";

// Dashboard charting. 
import { ChartsModule } from 'ng2-charts';

// Services. 
import { AuthGuardService } from "./services/auth.guard.service";
import { AuthService } from "./services/auth.service";
import { ConfigurationService } from "./services/config.service";
import { DocumentService } from "./services/document.service";
import { InvoiceService } from "./services/invoice.service";
import { HostPriceService } from "./services/host_pricing.service";
import { HostService } from "./services/host.service";
import { VendorService } from "./services/vendor.service";

// Generated REST API.
import { BASE_PATH } from "./client/variables";
import { Configuration } from "./client/configuration";
import { SecurityApi } from "./client/api/SecurityApi";
import { InvoiceApi } from "./client/api/InvoiceApi";
import { DocumentApi } from "./client/api/DocumentApi";

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        AssetSpendComponent,
        IndustryBaselineComponent,
        DocumentsComponent,
        InvoicesComponent,
        InventoryComponent,
        InventoryHostsComponent,
        PricesComponent,
        PricesHostsComponent,
        VendorsComponent,
        ErrorModalComponent,
        AboutComponent
    ],
    imports: [
        BrowserAnimationsModule,
        BrowserModule,
        FormsModule,
        HttpModule,
        ClarityModule,
        ChartsModule,
        ROUTING
    ],
    providers: [
      { provide: BASE_PATH, useValue: 'https://localhost:8443/api/v1' },
      AuthGuardService,
      AuthService,
      ConfigurationService,
      DocumentService,
      InvoiceService,
      HostService,
      HostPriceService,
      VendorService, 
      Configuration,
      SecurityApi,
      DocumentApi,
      InvoiceApi,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
