/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { HostService, Host }   from '../services/host.service';

@Component({
    selector: 'my-app-hosts', 
    templateUrl: './hosts.component.html',
    styles: []
})
export class HostsComponent implements OnInit {
  // Model controls. 

  // Host listing.
  selected: Host[] = [];
  hosts: Host[] = [];

  constructor(
    private router: Router,
    private hostService: HostService
  ) {}

  ngOnInit(): void {
    this.getHosts();
  }

  getHosts(): void {
    this.hosts = this.hostService.getHosts();
    console.log("Total hosts: " + this.hosts.length);
  }
}
