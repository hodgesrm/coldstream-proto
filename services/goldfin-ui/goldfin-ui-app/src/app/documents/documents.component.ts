/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { DocumentService }   from '../services/document.service';

import { Document } from '../client/model/Document';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'documents', 
    templateUrl: './documents.component.html', 
    styles: []
})
export class DocumentsComponent implements OnInit {
  // Model controls. 
  delete_open: boolean = false;
  import_open: boolean = false;

  // Error reporter sub-component.
  errorReporter: ErrorReporter = new ErrorReporter();

  // Document listing.
  selected: Document[] = [];
  documents: Document[] = [];

  constructor(
    private router: Router,
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

  onImport(): void {
    console.log("onImport invoked");
  }

  onImportFiles(event): void {
    console.log("onImport invoked");
    for (let file of event.target.files) {
      console.log(file); 
    }
  }

  onDelete(): void {
    console.log("onDelete invoked");
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items to delete";
      this.errorReporter.error_open = true;
    } else {
      this.delete_open = true;
    }
 
  }
}
