/*
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { saveAs } from 'file-saver/FileSaver';

import { ExtractService }   from '../services/extract.service';
import { InvoiceService }   from '../services/invoice.service';
import { Invoice } from '../client/model/models';
import { InvoiceItem } from '../client/model/models';
import { InvoiceValidationResult } from '../client/model/models';

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

export class ResourceGroup {
  // Summary values for a single resource
  resourceId: string;
  totalAmount: number;
  currency: string;
  startDate: Date;
  endDate: Date;
  items: InvoiceJoinedToItem[] = [];
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
  resource_view_open: boolean = false;
  invoice_validations_open: boolean = false;

  // Error reporter sub-component. 
  errorReporter: ErrorReporter = new ErrorReporter();

  // Invoice listing.
  selected: Invoice[] = [];
  invoices: Invoice[] = [];

  // Invoice view listing.
  invoice_items: InvoiceJoinedToItem[] = [];

  // Invoice item listing.
  resource_items: ResourceGroup[] = [];

  // Invoice validation listing.  The first value is the full 
  // set of validations.  We refine this to the filtered
  // validation list, which includes all validations if 
  // show_all_validations is true; otherwise only failures appear.
  invoice_validations: InvoiceValidationResult[] = [];
  filtered_validations: InvoiceValidationResult[] = [];
  show_all_validations: boolean = false;

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
    this.resource_items = [];
    var resource_map = {};

    // Create the materialized list of invoice line items. 
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

        // Now that we have the joined object, add it to the resource 
        // map. 
        var searchId: string;
        if (joined.resourceId == null) {
          searchId = "[No Resource ID]";
        } else {
          searchId = joined.resourceId;
        }
        var resourceGroup: ResourceGroup = resource_map[searchId];
        if (resourceGroup == null) {
          resourceGroup = new ResourceGroup();
          resourceGroup.resourceId = searchId;
          resourceGroup.totalAmount = 0.0;
          resource_map[searchId] = resourceGroup;
        }
        // Add invoice item total amount if present. 
        if (joined.totalAmount != null) {
          resourceGroup.totalAmount += joined.totalAmount;
        }
        // Add the invoice itself to the list. 
        resourceGroup.items.push(joined);
      }
    }

    // Get the keys of the resource map and create a list of resource
    // groups. 
    for (var key in resource_map) {
      var resourceGroup: ResourceGroup = resource_map[key];
      this.resource_items.push(resourceGroup);
    }
  }

  // Truncates input value to number of characters requested.
  truncateDisplayValue(value: string, length: number): string {
      if (value.length <= length) {
          return value;
      } else {
          return value.substring(0, length - 1) + "...";
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

  onResourceView(): void {
    console.log("onResourceView invoked");
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more invoices";
      this.errorReporter.error_open = true;
    } else {
      this.populateInvoiceItems();
      this.resource_view_open = true;
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
              this.filterValidations();
              this.invoice_validations_open = true;
              console.log("Opened invoice validations");
            }
          });
      }
    }
  }

  filterValidations() {
    var filtered_validations = [];
    for (let validation of this.invoice_validations) {
      if (! validation.passed || this.show_all_validations) {
        filtered_validations.push(validation);
      }
    }
    this.filtered_validations = filtered_validations;
  }

  onValidationToggleChange(event) {
    var checked = event.srcElement.checked;
    console.log("Validation toggle switch value change: " + checked);
    this.show_all_validations = checked;
    this.filterValidations();
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
          .subscribe(
            response => {
              if (response) {
                console.log("Got download response: " + response.name);
                saveAs(response.blob, response.name);
              }
            },
            error => {
              this.errorReporter.error_message = error.message;
              this.errorReporter.error_open = true;
            }
          );
      }
    }
  }

  onExportHeaders(): void {
    console.log("onExportHeaders invoked " + this.selected);
    this.extractService.fetchInvoiceCsv()
      .subscribe(response => {
        console.log("Saving export headers CSV output");
        saveAs(response.body, 'extract.csv');
      });
  }

  onExportDetails(): void {
    console.log("onExportDetails invoked " + this.selected);
    this.extractService.fetchInvoiceItemCsv()
      .subscribe(response => {
        console.log("Saving export details CSV output");
        saveAs(response.body, 'extract_item.csv');
      });
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
