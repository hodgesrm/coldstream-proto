/*
 * Copyright (c) 2017 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { InvoiceService, Invoice }   from '../services/invoice.service';


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
    this.invoiceService.getInvoices().then(invoices => this.invoices = invoices);
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
