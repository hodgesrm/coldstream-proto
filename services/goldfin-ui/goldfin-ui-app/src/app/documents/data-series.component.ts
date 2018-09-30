/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { DataSeriesService }   from '../services/data-series.service';

import { DataSeries } from '../client/model/models';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

import { UploadRequest } from '../utility/file-upload-modal.component';

@Component({
    selector: 'data-series', 
    templateUrl: './data-series.component.html', 
    styles: []
})
export class DataSeriesComponent implements OnInit {
  // Model controls. 
  file_upload_open: boolean = false;
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

  onUpload(): void {
    console.log("onUpload invoked");
    this.file_upload_open = true;
  }

  uploadFile(request: UploadRequest): void {
    console.log("Upload file: " + request.files);
    console.log("Upload description: " + request.description);
    var component = this;
    this.dataSeriesService.uploadDataSeries(request.files, request.description)
      .then(function() {
        console.log("Submitted");
        component.getDataSeries();
    });
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
