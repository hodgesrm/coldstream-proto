/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable, OnInit } from '@angular/core';

export class Vendor {
  identifier: string;
  name: string;
  state: string;
  creationDate: string;
}

const VENDORS: Vendor[] = [ 
  { identifier: "AWS", 
    name: "Amazon Web Services", 
    state: "ACTIVE", 
    creationDate: "2016-1-15"
  },
  { identifier: "Internap Corporation", 
    name: "Internap", 
    state: "ACTIVE", 
    creationDate: "2017-01-25"
  },
  { identifier: "OVH.com", 
    name: "OVH", 
    state: "ACTIVE", 
    creationDate: "2016-12-01"
  }
];

@Injectable()
export class VendorService {
  // In-memory vendors and vendor aggregates. 
  vendors: Vendor[] = [];

  getVendors(): Vendor[] {
    return VENDORS;
  }
}
