/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from "@angular/core";

import { InvoiceService, Invoice }   from '../services/invoice.service';


@Component({
    styleUrls: ['./home.component.scss'],
    templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  constructor(
    private invoiceService: InvoiceService
  ) {}

  // Invoice data. 
  invoices: Invoice[] = [];
  recentInvoices: Invoice[] = [];
  vendors: string[] = [];

  // Bar chart for on-going spend. 
  public vendorSpendOptions:any = {
    responsive: true,
    fill: false
  };
  public vendorSpendLabels:string[] = [];
  public vendorSpendData:any[] = [];
  public vendorSpendType:string = 'line';
  public vendorSpendLegend:boolean = true;

  // Populate vendor chart data on startup. 
  public ngOnInit(): void {
    this.invoices = this.invoiceService.fetchInvoices();
    this.recentInvoices = this.invoiceService.getRecentInvoices();
    this.vendors = this.invoiceService.getInvoiceVendors();
    this.populateItSpend();
  }

  populateItSpend(): void {
    // Generate labels as well as a spare array of monthly totals.
    this.vendorSpendLabels = [];
    let monthlyTotals = [];

    for (var i = 0; i < 12; i++) {
      let monthLabel = new Date(2017, i, 1).toISOString().substring(0, 7);
      let monthTotal = {};
      this.vendorSpendLabels.push(monthLabel);
      for (var j = 0; j < this.vendors.length; j++) {
        monthTotal[this.vendors[j]] = 0;
      }
      monthlyTotals.push(monthTotal);
    }

    // Read all invoices and get the monthly totals. 
    for (var i = 0; i < this.invoices.length; i++) {
      let invoice = this.invoices[i];
      console.log(invoice.effective_date);
      let effective_date = new Date(invoice.effective_date);
      let month = effective_date.getUTCMonth(); 
      let monthTotal = monthlyTotals[month];

      monthTotal[invoice.vendor] += invoice.total_amount;
    }

    // Generate data arrays for each vendor. 
    for (var i = 0; i < this.vendors.length; i++) {
      let vendorData = {};
      vendorData['label'] = this.vendors[i];
      vendorData['lineTension'] = 0;
      vendorData['fill'] = false;
      vendorData['data'] = [];
      for (var j = 0; j < monthlyTotals.length; j++) {
        let monthTotal = monthlyTotals[j];
        vendorData['data'].push(monthTotal[this.vendors[i]]);
      }
      this.vendorSpendData.push(vendorData);
    }
  }
    
  // events
  public chartClicked(e:any):void {
    console.log(e);
  }
 
  public chartHovered(e:any):void {
    console.log(e);
  }
 
  // IT Spend comparison radar chart. 
  public costBenchmarkChartOptions:any = {
    scaleShowVerticalLines: false,
    responsive: true
  };
  public costBenchmarkChartLabels:string[] = ['Amazon', 'OVH.com', 'Internap Incorporated'];
  public costBenchmarkChartData:any[] = [
    {data: [101901, 236521, 7567], label: 'Your Costs'}, 
    {data: [120965, 197554, 6023], label: 'Benchmark vs. Similar Inventory'}
  ];
  public costBenchmarkChartType:string = 'bar';
 
  // events
  public chartClickedCostBenchmark(e:any):void {
    console.log(e);
  }
 
  public chartHoveredCostBenchmark(e:any):void {
    console.log(e);
  }
}
