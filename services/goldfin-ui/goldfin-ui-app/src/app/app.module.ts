import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ClarityModule } from '@clr/angular';
import { Router } from '@angular/router';

import { AppComponent } from './app.component';
import { ROUTING } from "./app.routing";
import { HomeComponent } from "./home/home.component";
import { AssetSpendComponent } from "./home/asset-spend.component";
import { IndustryBaselineComponent } from "./home/industry-baseline.component";
import { LoginComponent } from "./login/login.component";
import { InvoicesComponent } from "./invoices/invoices.component";
import { SourcesComponent } from "./documents/sources.component";
import { DocumentsComponent } from "./documents/documents.component";
import { DataSeriesComponent } from "./documents/data-series.component";
import { FileUploadModalComponent } from "./utility/file-upload-modal.component";
import { InventoryComponent } from "./inventory/inventory.component";
import { HostsComponent } from "./inventory/hosts.component";
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
// Commenting out until code works.
import { AuthInterceptor } from "./services/auth.interceptor";
import { DocumentService } from "./services/document.service";
import { DataSeriesService } from "./services/data-series.service";
import { ExtractService } from "./services/extract.service";
import { InvoiceService } from "./services/invoice.service";
import { HostPriceService } from "./services/host_pricing.service";
import { HostService } from "./services/host.service";
import { VendorService } from "./services/vendor.service";

// Provider factories. 
import { configurationFactory } from "./services/provider.factories";
import { baseUrlFactory } from "./services/provider.factories";
// Commenting out until code works.
import { httpInterceptorFactory } from "./services/provider.factories";

// Generated REST API.
import { BASE_PATH } from "./client/variables";
import { Configuration } from "./client/configuration";
import { DataService as DataApi } from "./client/api/api";
import { DocumentService as DocumentApi } from "./client/api/api";
import { ExtractService as ExtractApi } from "./client/api/api";
import { InventoryService as InventoryApi } from "./client/api/api";
import { InvoiceService as InvoiceApi } from "./client/api/api";
import { SecurityService as SecurityApi } from "./client/api/api";
import { VendorService as VendorApi } from "./client/api/api";

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        AssetSpendComponent,
        IndustryBaselineComponent,
        SourcesComponent,
        DocumentsComponent,
        DataSeriesComponent,
        FileUploadModalComponent,
        InvoicesComponent,
        InventoryComponent,
        HostsComponent,
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
        HttpClientModule,
        ClarityModule,
        ChartsModule,
        ROUTING
    ],
    providers: [
      { provide: Configuration, useFactory: configurationFactory },
      { provide: BASE_PATH, useFactory: baseUrlFactory },
      {
         provide: HTTP_INTERCEPTORS,
         useFactory: httpInterceptorFactory, 
         multi: true,
         deps: [Router]
      },
      // Local application services.
      AuthGuardService,
      AuthService,
      DocumentService,
      DataSeriesService,
      ExtractService,
      InvoiceService,
      HostService,
      HostPriceService,
      VendorService, 
      // Generated client services.
      DataApi,      
      DocumentApi,
      ExtractApi,
      InventoryApi,
      InvoiceApi,
      SecurityApi,
      VendorApi
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
