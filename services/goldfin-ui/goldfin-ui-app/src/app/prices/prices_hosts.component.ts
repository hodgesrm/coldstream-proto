/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { HostPriceService, HostPrice }   from '../services/host_pricing.service';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'my-prices-hosts', 
    templateUrl: './prices_hosts.component.html',
    styles: []
})
export class PricesHostsComponent implements OnInit {
  // Model controls. 

  // Host listing.
  selected: HostPrice[] = [];
  host_prices: HostPrice[] = [];

  // Error reporter sub-component.
  errorReporter: ErrorReporter = new ErrorReporter();

  constructor(
    private router: Router,
    private hostPriceService: HostPriceService
  ) {}

  ngOnInit(): void {
    this.getHostPrices();
  }

  getHostPrices(): void {
    this.host_prices = this.hostPriceService.getHostPrices();
    console.log("Total host prices: " + this.host_prices.length);
  }

  onAnalyze(): void {
    console.log("onAnalyze invoked");
    this.errorReporter.error_message = "Price analysis is not implemented yet";
    this.errorReporter.error_open = true;
  }

  onExport(): void {
    console.log("onExport invoked");
    this.errorReporter.error_message = "Price export is not implemented yet";
    this.errorReporter.error_open = true;
  }
}
