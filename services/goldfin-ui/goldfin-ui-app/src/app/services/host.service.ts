/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { InventoryApi } from '../client/api/InventoryApi';
import { Host } from '../client/model/Host';

@Injectable()
export class HostService {
  constructor(
    private inventoryApi: InventoryApi
  ) {}

  loadHosts(): Observable<Array<Host>> {
    return this.inventoryApi.hostShowAll(true);
  }

  deleteHosts(invoices: Host[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < invoices.length; i++) {
      var invoice = invoices[i];
      console.log("Delete scheduled: " + invoice.id);
      var next = this.inventoryApi.hostDelete(invoice.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }
}
