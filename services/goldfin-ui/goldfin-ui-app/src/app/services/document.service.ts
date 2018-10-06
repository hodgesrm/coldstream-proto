/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { map, catchError } from 'rxjs/operators';
import { ResponseContentType } from '@angular/http';
import { HttpResponse, HttpErrorResponse, HttpEvent } from '@angular/common/http';

import { DocumentService as DocumentApi } from '../client/api/api';
import { Document } from '../client/model/models';

@Injectable()
export class DocumentService {
  constructor(
    private documentApi: DocumentApi
  ) {}

  createDocument(file: File, description: string): Observable<Document> {
    return this.documentApi.documentCreate(file, description);
  }

  loadDocuments(): Observable<Array<Document>> {
    return this.documentApi.documentShowAll();
  }

  getBase64(file: File): Promise<{}> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  uploadDocuments(files: File[], description): Array<Observable<HttpEvent<Document>>> {
    var observables = [];
    var component = this;
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      console.log("Upload queued: " + file.name);
      var next = component.documentApi.documentCreate(
                 file, description, null, true, 'events', 
                 true);
      observables.push(next);
    }
    return observables;
  }

  downloadDocuments(documentIds: string[]): Array<Observable<any>> {
    var observables = [];
    for (let documentId of documentIds) {
      // Map the response to a tuple consisting of the blob + file name. 
      var download = this.documentApi.documentDownload(documentId, 
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

  scanDocuments(documents: Document[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < documents.length; i++) {
      var doc = documents[i];
      console.log("Scan scheduled: " + doc.id);
      var next = this.documentApi.documentProcess(doc.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }

  deleteDocuments(documents: Document[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < documents.length; i++) {
      var doc = documents[i];
      console.log("Delete scheduled: " + doc.id);
      var next = this.documentApi.documentDelete(doc.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }
}
