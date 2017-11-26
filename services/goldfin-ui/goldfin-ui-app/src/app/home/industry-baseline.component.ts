/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from "@angular/core";

@Component({
    selector: 'industry-baseline', 
    styleUrls: ['./industry-baseline.component.scss'],
    templateUrl: './industry-baseline.component.html',
})
export class IndustryBaselineComponent implements OnInit {
  constructor(
  ) {}

  // IT Spend comparison radar chart. 
  public costBenchmarkChartOptions:any = {
    scaleShowVerticalLines: false,
    responsive: true
  };
  public costBenchmarkChartLabels:string[] = ['Amazon', 'OVH.com', 'Internap Incorporated'];
  public costBenchmarkChartData:any[] = [
    {data: [101901, 236521, 7567], label: 'Your Costs'}, 
    {data: [120965, 197554, 6023], label: 'Industry Baseline Costs'}
  ];
  public costBenchmarkChartType:string = 'bar';
 
  public ngOnInit():void {
    // Fill in later.
  }

  // events
  public chartClickedCostBenchmark(e:any):void {
    console.log(e);
  }
 
  public chartHoveredCostBenchmark(e:any):void {
    console.log(e);
  }
}
