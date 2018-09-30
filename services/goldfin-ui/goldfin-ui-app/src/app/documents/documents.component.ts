/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { DocumentService }   from '../services/document.service';

import { saveAs } from 'file-saver/FileSaver';

import { Document } from '../client/model/models';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

import { UploadRequest } from '../utility/file-upload-modal.component';

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
    var component = this;
    this.documentService.uploadDocuments(request.files, request.description)
      .then(function() {
        console.log("Submitted");
        component.getDocuments();
    });
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
          .subscribe(response => {
            console.log("Got download response");
            var blob = new Blob([response.blob()], { type: 'application/octet-stream' });
            // Find the file name. 
            var fileName = 'document.pdf';
            var contentDisposition: string = response.headers.get('Content-Disposition');
            var quotedName = contentDisposition.split(';')[1].trim().split('=')[1];
            fileName = quotedName.replace(/"/g, '');
            saveAs(blob, fileName);
          });
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
