/*
 * Copyright (c) 2017 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { InvoiceService }   from '../services/invoice.service';
import { Invoice } from '../client/model/Invoice';
import { InvoiceItem } from '../client/model/InvoiceItem';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

export class InvoiceJoinedToItem {
  // Values derived from invoice.
  invoice_documentId?: string;
  invoice_description?: string;
  invoice_identifier?: string;
  invoice_effectiveDate?: Date;
  invoice_vendor?: string;
  invoice_subtotalAmount?: number;
  invoice_tax?: number;
  invoice_totalAmount?: number;
  invoice_currency?: string;

  // Values derived from item. 
  itemId?: string;
  resourceId?: string;
  description?: string;
  unitAmount?: number;
  units?: number;
  totalAmount?: number;
  currency?: string;
  startDate?: Date;
  endDate?: Date;
  oneTimeCharge?: boolean;
  inventoryId?: string;
  inventoryType?: InvoiceItem.InventoryTypeEnum;
}

@Component({
    selector: 'invoices', 
    templateUrl: './invoices.component.html', 
    styles: []
})
export class InvoicesComponent implements OnInit {
  // Model controls. 
  delete_open: boolean = false;
  invoice_detail_open: boolean = false;

  // Error reporter sub-component. 
  errorReporter: ErrorReporter = new ErrorReporter();

  // Invoice listing.
  selected: Invoice[] = [];
  invoices: Invoice[] = [];

  // Invoice item listing.
  invoice_items: InvoiceJoinedToItem[] = [];

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

  populateInvoiceItems(): void {
    this.invoice_items = [];

    for (let invoice of this.selected) {
      for (let item of invoice.items) {
        var joined = new InvoiceJoinedToItem();
          // Values from invoice header.
          joined.invoice_documentId = invoice.documentId;
          joined.invoice_description = invoice.description;
          joined.invoice_identifier = invoice.identifier;
          joined.invoice_effectiveDate = invoice.effectiveDate;
          joined.invoice_vendor = invoice.vendor;
          joined.invoice_subtotalAmount = invoice.subtotalAmount;
          joined.invoice_tax = invoice.tax;
          joined.invoice_totalAmount = invoice.totalAmount;
          joined.invoice_currency = invoice.currency;

          // Values from invoice item. 
          joined.itemId = item.itemId;
          joined.resourceId = item.resourceId;
          joined.description = item.description;
          joined.unitAmount = item.unitAmount;
          joined.units = item.units;
          joined.totalAmount = item.totalAmount;
          joined.currency = item.currency;
          joined.startDate = item.startDate;
          joined.endDate = item.endDate;
          joined.oneTimeCharge = item.oneTimeCharge;
          joined.inventoryId = item.inventoryId;
          joined.inventoryType = item.inventoryType;

          this.invoice_items.push(joined);
      }
    }
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getInvoices();
  }

  onInvoiceDetail(): void {
    console.log("onInvoiceDetail invoked");
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
      this.errorReporter.error_open = true;
    } else {
      this.populateInvoiceItems();
      this.invoice_detail_open = true;
    }
  }

  onExport(): void {
    console.log("onExport invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
      this.errorReporter.error_open = true;
    } else {
      this.errorReporter.error_message = "Export is not implemented yet";
      this.errorReporter.error_open = true;
    }
  }

  onDelete(): void {
    console.log("onDelete invoked");
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
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
