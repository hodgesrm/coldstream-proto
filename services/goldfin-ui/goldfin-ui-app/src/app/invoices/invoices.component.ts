/*
 * Copyright (c) 2017 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { InvoiceService }   from '../services/invoice.service';

import { Invoice } from '../client/model/Invoice';

@Component({
    selector: 'invoices', 
    templateUrl: './invoices.component.html', 
    styles: []
})
export class InvoicesComponent implements OnInit {
  // Model controls. 
  delete_open: boolean = false;
  import_open: boolean = false;

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

  onImport(): void {
    console.log("onImport invoked");
  }

  onImportFiles(event): void {
    console.log("onImport invoked");
    for (let file of event.target.files) {
      console.log(file); 
    }
  }

  onExport(): void {
    console.log("onExport invoked " + this.selected);
  }

  onDelete(): void {
    console.log("onDelete invoked");
  }
}
