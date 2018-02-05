/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';

export class Invoice {
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
  invoices: Invoice[] = null;

  fetchInvoices(): Invoice[] {
    if (this.invoices === null) {
      console.log("Initializing invoices");
      this.invoices = this._fetchInvoices();
      console.log("Initialized invoices: length=" + this.invoices.length);
    }
    return this.invoices;
  }

  // Generate invoices. 
  _fetchInvoices(): Invoice[] {
    // Generates invoices locally for now. 
    console.log("Fetching invoices");
    let invoiceGenerator = function(identifierPrefix, amount, vendor) {
      // Generate invoices with up to 20% variation in amount. 
      var invoices: Invoice[] = [];
      var i;
      for (i = 0; i < 12; i++) {
        var invoice = new Invoice();
        invoice.identifier = identifierPrefix + (i * 1000 / 100); 
        invoice.effective_date = new Date(2017, i, 25).toISOString().substring(0,10);
        invoice.vendor = vendor;
        invoice.subtotal_amount = _roundCurrency(amount * (9 + (2 * Math.random())) / 10);
        invoice.tax = 0.00;
        invoice.total_amount = invoice.subtotal_amount;
        invoice.currency = 'USD';
        invoices.push(invoice);
        console.log("Added invoice: identifier=" + invoice.identifier + 
          " invoices size=" + invoices.length);
      }
      return invoices;
    }

    // Generate on a per vendor basis.  
    var vendorParams = [
      {prefix: "WE666", amount: 423.01, vendor: "OVH.com"}, 
      {prefix: "INV-14066-486", amount: 16932.40, vendor: "Internap Corporation"},
      {prefix: "AWS-2017-", amount: 8500.00, vendor: "AWS"}
    ];
 
    var allInvoices: Invoice[] = [];
    for (let params of vendorParams) {
      for (let vendorInvoices of invoiceGenerator(params.prefix, 
          params.amount, params.vendor)) {
        allInvoices = allInvoices.concat(vendorInvoices);
        console.log("Total invoices size=" + allInvoices.length);
      }
    }
    return allInvoices;
  }

  getInvoiceVendors(): string[]  {
    let vendors = {};
    for (let invoice of this.fetchInvoices()) {
      vendors[invoice.vendor] = "x";
    }
    return Object.keys(vendors);
  }

  getInvoices(): Promise<Invoice[]> {
    return Promise.resolve(this.fetchInvoices());
  }

  // Get Invoices from last N days. 
  getRecentInvoices(): Invoice[] {
    let recentInvoices: Invoice[] = [];
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

  getInvoice(identifier: string): Invoice {
    return this.fetchInvoices().find(invoice => invoice.identifier === identifier);
  } 
}

function _roundCurrency(amount: number): number {
  return (Math.round(amount * 100)) / 100;
}
