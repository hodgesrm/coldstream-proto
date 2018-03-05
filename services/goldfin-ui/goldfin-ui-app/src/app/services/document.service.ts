/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { DocumentApi } from '../client/api/DocumentApi';
import { Document } from '../client/model/Document';

@Injectable()
export class DocumentService {
  constructor(
    private documentApi: DocumentApi
  ) {}

  loadDocuments(): Observable<Array<Document>> {
    return this.documentApi.documentShowAll(false);
  }

  scanDocuments(documents: Document[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < documents.length; i++) {
      var doc = documents[i];
      console.log("Scan scheduled: " + doc.id);
      var next = this.documentApi.documentScan(doc.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }
}
