/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { VendorService as VendorApi } from '../client/api/api';
import { Vendor } from '../client/model/models';

@Injectable()
export class VendorService {
  constructor(
    private vendorApi: VendorApi
  ) {}

  loadVendors(): Observable<Array<Vendor>> {
    return this.vendorApi.vendorShowall();
  }

  deleteVendors(vendors: Vendor[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < vendors.length; i++) {
      var vendor = vendors[i];
      console.log("Delete scheduled: " + vendor.id);
      var next = this.vendorApi.vendorDelete(vendor.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }
}
