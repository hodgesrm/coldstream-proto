/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { VendorService, Vendor }   from '../services/vendor.service';

@Component({
    selector: 'vendors', 
    templateUrl: './vendors.component.html', 
    styles: []
})

export class VendorsComponent implements OnInit {
  // Model controls. 
  import_open: boolean = false;
  create_open: boolean = false;
  update_open: boolean = false;
  delete_open: boolean = false;

  // Vendor listing.
  selected: Vendor[] = [];
  vendors: Vendor[] = [];

  constructor(
    private router: Router,
    private vendorService: VendorService
  ) {}

  ngOnInit(): void {
    this.getVendors();
  }

  getVendors(): void {
    this.vendors = this.vendorService.getVendors();
    console.log("Total vendors: " + this.vendors.length);
  }

  onCreate(): void {
    console.log("onCreate invoked");
  }

  onUpdate(): void {
    console.log("onUpdate invoked");
  }

  onExport(): void {
    console.log("onExport invoked");
  }

  onDelete(): void {
    console.log("onDelete invoked");
  }
}
