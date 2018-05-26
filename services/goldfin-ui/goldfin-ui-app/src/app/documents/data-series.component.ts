/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { DataSeriesService }   from '../services/data-series.service';

import { DataSeries } from '../client/model/DataSeries';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'data-series', 
    templateUrl: './data-series.component.html', 
    styles: []
})
export class DataSeriesComponent implements OnInit {
  // Model controls. 
  upload_open: boolean = false;
  delete_open: boolean = false;

  // Error reporter sub-component.
  errorReporter: ErrorReporter = new ErrorReporter();

  // DataSeries listing.
  selected: DataSeries[] = [];
  dataSeries: DataSeries[] = [];

  constructor(
    private dataSeriesService: DataSeriesService
  ) {}

  ngOnInit(): void {
    this.getDataSeries();
  }

  getDataSeries(): void {
    this.dataSeriesService.loadDataSeries()
      .subscribe(newDataSeries => {
        this.dataSeries = newDataSeries; 
        console.log("Total data series: " + this.dataSeries.length);
      });
  }

  onProcess(): void {
    console.log("onProcess invoked");
    var component = this;
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      this.dataSeriesService.processDataSeries(this.selected)
        .then(function() {
          component.getDataSeries();
        });
    }
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getDataSeries();
  }

  onImport(): void {
    console.log("onImport invoked");
  }

  onImportFiles(event): void {
    console.log("onImport invoked");
    var files = event.target.files;
    for (let file of event.target.files) {
      console.log(file); 
    }
  }

  onUpload(): void {
    console.log("onUpload invoked");
    this.upload_open = true;
  }

  onDelete(): void {
    console.log("onDelete invoked");
    var component = this;
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      this.delete_open = true;
    }
  }

  onDeleteConfirmed(): void {
    console.log("onDeleteConfirmed invoked");
    var component = this;
    this.dataSeriesService.deleteDataSeries(this.selected)
      .then(function() {
        component.getDataSeries();
      });
    this.delete_open = false;
  }
}
