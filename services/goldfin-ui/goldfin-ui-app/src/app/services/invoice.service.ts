/*
 * Copyright (c) 2017-2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { map, catchError } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

import { InvoiceService as InvoiceApi } from '../client/api/api';
import { Invoice } from '../client/model/models';
import { InvoiceValidationResult } from '../client/model/models';

export class FakeInvoice {
  identifier: string;
  effective_date: string;
  vendor: string;
  subtotal_amount: number;
  tax: number;
  total_amount: number;
  currency: string;
}

@Injectable()
export class InvoiceService {
  // In-memory invoices and invoice aggregates. 
  invoices: FakeInvoice[] = null;

  constructor(
    private invoiceApi: InvoiceApi
  ) {}

  loadInvoices(): Observable<Array<Invoice>> {
    return this.invoiceApi.invoiceShowAll(true);
  }

  downloadInvoices(invoiceIds: string[]): Array<Observable<any>> {
    var observables = [];

    for (let invoiceId of invoiceIds) {
      // Map the response to a tuple consisting of the blob + file name.
      var download = this.invoiceApi.invoiceDownload(invoiceId,
          'response').pipe(
        map<HttpResponse<Blob>, any>(response => {
          console.log("Processing response");
          var contentMap = {};
          contentMap['blob'] = response.body;
          // Find the file name, which is in a header of the following form:
          // Content-Disposition: attachment; filename="<name>"
          var fileName = 'document.pdf';
          var contentDisposition: string = response.headers.get('Content-Disposition');
          var quotedName = contentDisposition.split(';')[1].trim().split('=')[1];
          fileName = quotedName.replace(/"/g, '');
          contentMap['name'] = fileName;
          return contentMap;
        }),
        catchError(error => {
          console.log("Error: " + error);
          return error;
        })
      );
      observables.push(download);
    }
    return observables;
  }

  // Run validation on all resources. 
  validateInvoices(invoiceIds: string[]): Array<Observable<Array<InvoiceValidationResult>>> {
    var observables = [];
    for (let invoiceId of invoiceIds) {
      var observable = this.invoiceApi.invoiceValidate(invoiceId, false);
      observables.push(observable);
    }
    return observables;
  }

  // Remaining calls are mocks. 
  fetchInvoices(): FakeInvoice[] {
    if (this.invoices === null) {
      console.log("Initializing invoices");
      this.invoices = this._fetchInvoices();
      console.log("Initialized invoices: length=" + this.invoices.length);
    }
    return this.invoices;
  }

  // Generate invoices. 
  _fetchInvoices(): FakeInvoice[] {
    // Generates invoices locally for now. 
    console.log("Fetching invoices");
    let invoiceGenerator = function(identifierPrefix, amount, vendor) {
      // Generate invoices with up to 20% variation in amount. 
      var invoices: FakeInvoice[] = [];
      var i;
      for (i = 0; i < 12; i++) {
        var invoice = new FakeInvoice();
        invoice.identifier = identifierPrefix + (i * 1000 / 100); 
        invoice.effective_date = new Date(2017, i, 25).toISOString().substring(0,10);
        invoice.vendor = vendor;
        invoice.subtotal_amount = _roundCurrency(amount * (9 + (2 * Math.random())) / 10);
        invoice.tax = 0.00;
        invoice.total_amount = invoice.subtotal_amount;
        invoice.currency = 'USD';
        invoices.push(invoice);
        //console.log("Added invoice: identifier=" + invoice.identifier + 
        //  " invoices size=" + invoices.length);
      }
      return invoices;
    }

    // Generate on a per vendor basis.  
    var vendorParams = [
      {prefix: "WE666", amount: 423.01, vendor: "OVH.com"}, 
      {prefix: "INV-14066-486", amount: 16932.40, vendor: "Internap Corporation"},
      {prefix: "AWS-2017-", amount: 8500.00, vendor: "AWS"}
    ];
 
    var allInvoices: FakeInvoice[] = [];
    for (let params of vendorParams) {
      for (let vendorInvoices of invoiceGenerator(params.prefix, 
          params.amount, params.vendor)) {
        allInvoices = allInvoices.concat(vendorInvoices);
      }
    }
    console.log("Total invoices size=" + allInvoices.length);
    return allInvoices;
  }

  getInvoiceVendors(): string[]  {
    let vendors = {};
    for (let invoice of this.fetchInvoices()) {
      vendors[invoice.vendor] = "x";
    }
    return Object.keys(vendors);
  }

  getInvoices(): Promise<FakeInvoice[]> {
    return Promise.resolve(this.fetchInvoices());
  }


  deleteInvoices(invoices: Invoice[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < invoices.length; i++) {
      var invoice = invoices[i];
      console.log("Delete scheduled: " + invoice.id);
      var next = this.invoiceApi.invoiceDelete(invoice.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }

  // Get Invoices from last N days. 
  getRecentInvoices(): FakeInvoice[] {
    let recentInvoices: FakeInvoice[] = [];
    var days = 90
    var now = Date.now(); 
    for (var i = 0; i < this.invoices.length; i++) {
      let effectiveDate = new Date(this.invoices[i].effective_date).valueOf();
      let diffInDays = (now - effectiveDate) / 1000 / 3600 / 24;
      if (diffInDays > 0 && diffInDays <= days) {
        recentInvoices.push(this.invoices[i]);
      }
    }
    return recentInvoices.sort(this.compareByDate).reverse();
  }

  compareByDate(i1, i2) {
    var effectiveDate1 = new Date(i1.effectiveDate).valueOf();
    var effectiveDate2 = new Date(i2.effectiveDate).valueOf();
    if (effectiveDate1 < effectiveDate2)
        return -1;
    else if (effectiveDate1 > effectiveDate2)
        return 1;
    else
        return 0;
  }

  getInvoice(identifier: string): FakeInvoice {
    return this.fetchInvoices().find(invoice => invoice.identifier === identifier);
  } 
}

function _roundCurrency(amount: number): number {
  return (Math.round(amount * 100)) / 100;
}
