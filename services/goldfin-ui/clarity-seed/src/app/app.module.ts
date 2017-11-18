import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { ClarityModule } from 'clarity-angular';
import { AppComponent } from './app.component';
import { ROUTING } from "./app.routing";
import { HomeComponent } from "./home/home.component";
import { LoginComponent } from "./login/login.component";
import { InvoicesComponent } from "./invoices/invoices.component";
import { InventoryComponent } from "./inventory/inventory.component";
import { VendorsComponent } from "./vendors/vendors.component";
import { AboutComponent } from "./about/about.component";

import { ChartsModule } from 'ng2-charts';

import { AuthService } from "./services/auth.service";
import { InvoiceService } from "./services/invoice.service";
import { VendorService } from "./services/vendor.service";

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        InvoicesComponent,
        InventoryComponent,
        VendorsComponent,
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
      AuthService,
      InvoiceService,
      VendorService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
