/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { DataSeriesService }   from '../services/data-series.service';
import { HttpEvent, HttpEventType } from '@angular/common/http';


import { DataSeries } from '../client/model/models';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

import { UploadRequest } from '../utility/file-upload-modal.component';
import { FileProgressReporter } from '../utility/file-progress-reporter';
import { FileProgressModalComponent } from '../utility/file-progress-modal.component';

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

  // File progress reporter sub-component. 
  progressReporter: FileProgressReporter = new FileProgressReporter();

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
    var observables = this.dataSeriesService.uploadDataSeries(request.files, request.description);
    for (var i = 0; i < observables.length; i++) {
      this.progressReporter.reset();
      this.progressReporter.progress_open = true;
      observables[i].subscribe(
        (event: HttpEvent<any>) => {
          switch (event.type) {
            case HttpEventType.UploadProgress:
              const pct = Math.round(100.0 * event.loaded / event.total);
              this.progressReporter.progress_pct = pct;
              console.log(`Upload progress: ${pct}% loaded`);
              break;
            case HttpEventType.Response:
              console.log('Upload complete', event.body);
              this.progressReporter.succeeded('Upload complete');
              this.getDataSeries();
          }
        },
        err => {
          console.log(err);
          if (err.error.message) {
            this.progressReporter.failed(err.error.message);
          } else {
            this.progressReporter.failed('Upload failed');
          }
        }
      );
    }
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
