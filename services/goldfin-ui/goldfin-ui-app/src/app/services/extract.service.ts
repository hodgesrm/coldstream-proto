/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { Response }   from '@angular/http';

import { ExtractApi } from '../client/api/ExtractApi';

@Injectable()
export class ExtractService {
  constructor(
    private extractApi: ExtractApi
  ) {}

  fetchInvoiceCsv(): Observable<Response> {
    return this.extractApi.extractDownloadWithHttpInfo("invoice", "", "csv")
      .map((response: Response) => {
        if (response.status === 200) {
          return response;
        } else {
          return undefined;
        }
      });
  }

  fetchInvoiceItemCsv(): Observable<Response> {
    return this.extractApi.extractDownloadWithHttpInfo("invoice_item", "", "csv")
      .map((response: Response) => {
        if (response.status === 200) {
          return response;
        } else {
          return undefined;
        }
      });
  }
}
