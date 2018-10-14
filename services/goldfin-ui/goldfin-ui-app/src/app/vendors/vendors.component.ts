/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { VendorService }   from '../services/vendor.service';
import { Vendor }   from '../client/model/models';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'vendors', 
    templateUrl: './vendors.component.html', 
    styles: []
})

export class VendorsComponent implements OnInit {
  // Model controls. 
  create_open: boolean = false;
  update_open: boolean = false;
  delete_open: boolean = false;

  // Error reporter sub-component.
  errorReporter: ErrorReporter = new ErrorReporter();

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
    this.vendorService.loadVendors()
      .subscribe(newVendors => {
        this.vendors = newVendors;
        console.log("Total vendors: " + this.vendors.length);
      });
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getVendors();
  }

  onCreate(): void {
    console.log("onCreate invoked");
    this.create_open = true;
  }

  onUpdate(): void {
    console.log("onUpdate invoked");
    this.errorReporter.error_message = "Vendor edit is not implemented yet";
    this.errorReporter.error_open = true;
  }

  onDelete(): void {
    console.log("onDelete invoked");
    var component = this;
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items to delete";
      this.errorReporter.error_open = true;
    } else {
      this.delete_open = true;
    }
  }

  onDeleteConfirmed(): void {
    console.log("onDeleteConfirmed invoked");
    var component = this;
    this.vendorService.deleteVendors(this.selected)
      .then(function() {
        component.getVendors();
      });
    this.delete_open = false;
  }
}
