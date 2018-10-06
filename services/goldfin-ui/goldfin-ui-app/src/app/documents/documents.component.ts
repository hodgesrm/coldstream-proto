/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { DocumentService }   from '../services/document.service';
import { HttpEvent, HttpEventType } from '@angular/common/http';

import { saveAs } from 'file-saver/FileSaver';

import { Document } from '../client/model/models';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

import { UploadRequest } from '../utility/file-upload-modal.component';

import { FileProgressReporter } from '../utility/file-progress-reporter';
import { FileProgressModalComponent } from '../utility/file-progress-modal.component';

@Component({
    selector: 'documents', 
    templateUrl: './documents.component.html', 
    styles: []
})
export class DocumentsComponent implements OnInit {
  // Model controls. 
  file_upload_open: boolean = false;
  delete_open: boolean = false;

  // Error reporter sub-component.
  errorReporter: ErrorReporter = new ErrorReporter();

  // File progress reporter sub-component. 
  progressReporter: FileProgressReporter = new FileProgressReporter();

  // Document listing.
  selected: Document[] = [];
  documents: Document[] = [];

  constructor(
    private documentService: DocumentService
  ) {}

  ngOnInit(): void {
    this.getDocuments();
  }

  getDocuments(): void {
    this.documentService.loadDocuments()
      .subscribe(newDocuments => {
        this.documents = newDocuments; 
        console.log("Total documents: " + this.documents.length);
      });
  }

  onScan(): void {
    console.log("onScan invoked");
    var component = this;
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more documents to scan";
      this.errorReporter.error_open = true;
    } else {
      this.documentService.scanDocuments(this.selected)
        .then(function() {
          component.getDocuments();
        });
    }
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getDocuments();
  }

  onUpload(): void {
    console.log("onUpload invoked");
    this.file_upload_open = true;
  }

  uploadFile(request: UploadRequest): void {
    console.log("Upload file: " + request.files);
    console.log("Upload description: " + request.description);
    this.file_upload_open = false;
    var observables = this.documentService.uploadDocuments(request.files, request.description);
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
              this.getDocuments();
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

  onDownload(): void {
    console.log("onDownload invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      // Put document IDs in an array.
      var documentIds: string[] = [];
      for (let document of this.selected) {
        documentIds.push(document.id);
      }
      console.log("Collected document Ids: " + documentIds);
      var observables = this.documentService.downloadDocuments(documentIds);
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

  onDelete(): void {
    console.log("onDelete invoked");
    var component = this;
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items to delete";
      this.errorReporter.error_open = true;
    } else {
      this.delete_open = true;
    }
  }

  onDeleteConfirmed(): void {
    console.log("onDeleteConfirmed invoked");
    var component = this;
    this.documentService.deleteDocuments(this.selected)
      .then(function() {
        component.getDocuments();
      });
    this.delete_open = false;
  }
}
