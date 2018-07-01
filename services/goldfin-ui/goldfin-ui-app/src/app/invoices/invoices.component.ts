/*
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { saveAs } from 'file-saver/FileSaver';

import { ExtractService }   from '../services/extract.service';
import { InvoiceService }   from '../services/invoice.service';
import { Invoice } from '../client/model/Invoice';
import { InvoiceItem } from '../client/model/InvoiceItem';
import { InvoiceValidationResult } from '../client/model/InvoiceValidationResult';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

export class InvoiceJoinedToItem {
  // Values derived from invoice.
  invoice_documentId?: string;
  invoice_description?: string;
  invoice_identifier?: string;
  invoice_effectiveDate?: Date;
  invoice_vendorIdentifier?: string;
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
  invoice_validations_open: boolean = false;

  // Error reporter sub-component. 
  errorReporter: ErrorReporter = new ErrorReporter();

  // Invoice listing.
  selected: Invoice[] = [];
  invoices: Invoice[] = [];

  // Invoice item listing.
  invoice_items: InvoiceJoinedToItem[] = [];

  // Invoice validation listing. 
  invoice_validations: InvoiceValidationResult[] = [];

  constructor(
    private router: Router,
    private invoiceService: InvoiceService,
    private extractService: ExtractService
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
          joined.invoice_vendorIdentifier = invoice.vendorIdentifier;
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

  onValidate(): void {
    console.log("onValidate invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
      this.errorReporter.error_open = true;
    } else {
      // Put invoice IDs in an array. 
      var invoiceIds: string[] = [];
      for (let invoice of this.selected) {
        invoiceIds.push(invoice.id);
      }
      console.log("Collected invoice Ids: " + invoiceIds);
      var observables = this.invoiceService.validateInvoices(invoiceIds);
      var expectedObservables = observables.length; 
      var actualObservables = 0;
      this.invoice_validations = [];
      for (let observable of observables) {
        observable
          .subscribe(newValidations => {
            this.invoice_validations = this.invoice_validations.concat(newValidations);
            actualObservables += 1
            console.log("Added more invoice validations: " + actualObservables);
            if (actualObservables >= expectedObservables) {
              this.invoice_validations_open = true;
              console.log("Opened invoice validations");
            }
          });
      }
    }
  }

  // Return span class for the validation result based on whether it passed.
  getValidationResultClass(passed: boolean): string {
    if (passed) {
      return "label label-success";
    } else {
      return "label label-danger";
    }
  }

  // Convert passed true/false to OK/FAILED.
  getValidationResultName(passed: boolean): string {
    if (passed) {
      return "OK";
    } else {
      return "FAILED";
    }
  }

  onDownload(): void {
    console.log("onDownload invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      // Put invoice IDs in an array.
      var invoiceIds: string[] = [];
      for (let invoice of this.selected) {
        invoiceIds.push(invoice.id);
      }
      console.log("Collected invoice Ids: " + invoiceIds);
      var observables = this.invoiceService.downloadInvoices(invoiceIds);
      for (let observable of observables) {
        observable
          .subscribe(response => {
            console.log("Got download response");
            var blob = new Blob([response.blob()], { type: 'application/octet-stream' });
            // Find the file name.
            var fileName = 'invoice.pdf';
            var contentDisposition: string = response.headers.get('Content-Disposition');
            var quotedName = contentDisposition.split(';')[1].trim().split('=')[1];
            fileName = quotedName.replace(/"/g, '');
            saveAs(blob, fileName);
          });
      }
    }
  }

  onExport(): void {
    console.log("onExport invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
      this.errorReporter.error_open = true;
    } else {
      this.extractService.fetchInvoiceCsv()
        .subscribe(response => {
          var text = response.text();
          console.log(text);
          var blob = new Blob([text], { type: 'text/csv' });
          saveAs(blob, 'extract.csv');
        });
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
