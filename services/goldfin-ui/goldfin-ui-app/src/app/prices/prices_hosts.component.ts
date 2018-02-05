/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { HostPriceService, HostPrice }   from '../services/host_pricing.service';

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
}
