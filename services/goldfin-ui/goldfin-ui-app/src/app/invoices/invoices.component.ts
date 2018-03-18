/*
 * Copyright (c) 2017 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { InvoiceService }   from '../services/invoice.service';
import { Invoice } from '../client/model/Invoice';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'invoices', 
    templateUrl: './invoices.component.html', 
    styles: []
})
export class InvoicesComponent implements OnInit {
  // Model controls. 
  delete_open: boolean = false;

  // Error reporter sub-component. 
  errorReporter: ErrorReporter = new ErrorReporter();

  // Invoice listing.
  selected: Invoice[] = [];
  invoices: Invoice[] = [];

  constructor(
    private router: Router,
    private invoiceService: InvoiceService
  ) {}

  ngOnInit(): void {
    this.getInvoices();
  }

  getInvoices(): void {
    this.invoiceService.loadInvoices()
      .subscribe(newInvoices => {
        this.invoices = newInvoices; 
        console.log("Total invoices: " + this.invoices.length);
      });
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getInvoices();
  }

  onExport(): void {
    console.log("onExport invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items to export";
      this.errorReporter.error_open = true;
    } else {
      this.errorReporter.error_message = "Export is not implemented yet";
      this.errorReporter.error_open = true;
    }
  }

  onDelete(): void {
    console.log("onDelete invoked");
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
    this.invoiceService.deleteInvoices(this.selected)
      .then(function() {
        component.getInvoices();
      });
    this.delete_open = false;
  }
}
