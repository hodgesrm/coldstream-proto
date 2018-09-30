/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ResponseContentType } from '@angular/http';

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

  uploadDocuments(files: File[], description): Promise<{}> {
    var promises = [];
    var component = this;
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      console.log("Upload scheduled: " + file.name);
      var next = component.documentApi.documentCreate(
                 file, description, null, true);
      next.subscribe(
        data => {console.log(data);},
        error => {console.log(error);}
      );
      return Promise.all(promises);
    }
  }

  downloadDocuments(documentIds: string[]): Array<Observable<Response>> {
    var observables = [];
    let extraHttpOptions = {responseType: ResponseContentType.Blob};
  
    for (let documentId of documentIds) {
      //var observable = this.documentApi.documentDownloadWithHttpInfo(
      //  documentId, extraHttpOptions);
      var observable = this.documentApi.documentDownload(documentId);
      observables.push(observable);
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
