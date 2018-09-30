/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { HttpResponse }   from '@angular/common/http';

import { ExtractService as ExtractApi } from '../client/api/api';

@Injectable()
export class ExtractService {
  constructor(
    private extractApi: ExtractApi
  ) {}

  fetchInvoiceCsv(): Observable<HttpResponse<Blob>> {
    return this.extractApi.extractDownload("invoice", "", "csv", 'response');
  }

  fetchInvoiceItemCsv(): Observable<HttpResponse<Blob>> {
    return this.extractApi.extractDownload("invoice_item", "", "csv", 
      'response');
  }
}
