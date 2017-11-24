/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from "@angular/core";

@Component({
    selector: 'asset-spend', 
    styleUrls: ['./asset-spend.component.scss'],
    templateUrl: './asset-spend.component.html',
})
export class AssetSpendComponent implements OnInit {
  constructor(
    // Add services later. 
  ) {}

  // Asset spending data. 
  inventoryCosts = [
    {vendor: "AWS", dedicated_hosts: 0.0, cloud_hosts: 7118.23, 
        private_clouds: 0.0, cloud_storage: 1131.98, 
        total: 8250.21},
    {vendor: "Internap", dedicated_hosts: 10080.11, cloud_hosts: 0.0, 
        private_clouds: 7285.59, cloud_storage: 0.0, 
        total: 17365.70},
    {vendor: "OVH.com", dedicated_hosts: 420.43, cloud_hosts: 0.0, 
        private_clouds: 0.0, cloud_storage: 0.0, 
        total: 420.43}
  ];

  // Populate vendor chart data on startup.
  public ngOnInit(): void {
    // Fill in later...
  }
}
