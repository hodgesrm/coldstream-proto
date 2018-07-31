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
import { ConfigurationService } from "./services/config.service";
import { DocumentService } from "./services/document.service";
import { DataSeriesService } from "./services/data-series.service";
import { ExtractService } from "./services/extract.service";
import { InvoiceService } from "./services/invoice.service";
import { HostPriceService } from "./services/host_pricing.service";
import { HostService } from "./services/host.service";
import { VendorService } from "./services/vendor.service";

// Provider factories. 
import { baseUrlFactory } from "./services/location.factory";


// Generated REST API.
import { BASE_PATH } from "./client/variables";
import { Configuration } from "./client/configuration";
import { SecurityApi } from "./client/api/SecurityApi";
import { ExtractApi } from "./client/api/ExtractApi";
import { InventoryApi } from "./client/api/InventoryApi";
import { InvoiceApi } from "./client/api/InvoiceApi";
import { VendorApi } from "./client/api/VendorApi";

// Subclassed from generated code. 
import { DocumentApiExtended } from "./services/DocumentApiExtended";
import { DataSeriesApiExtended } from "./services/DataSeriesApiExtended";

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
        HttpModule,
        ClarityModule,
        ChartsModule,
        ROUTING
    ],
    providers: [
      { provide: BASE_PATH, useFactory: baseUrlFactory },
      AuthGuardService,
      AuthService,
      ConfigurationService,
      DocumentService,
      DataSeriesService,
      ExtractService,
      InvoiceService,
      HostService,
      HostPriceService,
      VendorService, 
      Configuration,
      SecurityApi,
      DocumentApiExtended,
      DataSeriesApiExtended,
      ExtractApi,
      InventoryApi,
      InvoiceApi,
      VendorApi
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
