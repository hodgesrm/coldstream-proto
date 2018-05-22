/*
 * Copyright (c) 2017 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router }   from '@angular/router'

import { HostService }   from '../services/host.service';
import { Host } from '../client/model/Host';

import { ErrorReporter } from '../utility/error-reporter';
import { ErrorModalComponent } from '../utility/error-modal.component';

@Component({
    selector: 'hosts', 
    templateUrl: './hosts.component.html', 
    styles: []
})
export class HostsComponent implements OnInit {
  // Model controls. 
  delete_open: boolean = false;

  // Error reporter sub-component. 
  errorReporter: ErrorReporter = new ErrorReporter();

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
    this.hostService.loadHosts()
      .subscribe(newHosts => {
        this.hosts = newHosts; 
        console.log("Total hosts: " + this.hosts.length);
      });
  }

  asGb(bytes: number): string {
    let gb = bytes / 1024 / 1024 / 1024;
    return gb.toString() + "GB"
  }

  onRefresh(): void {
    console.log("onRefresh invoked");
    this.getHosts();
  }

  onHostDetail(): void {
    console.log("onHostDetail invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      this.errorReporter.error_message = "Host detail view is not implemented yet";
      this.errorReporter.error_open = true;
    }
  }

  onExport(): void {
    console.log("onExport invoked " + this.selected);
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      this.errorReporter.error_message = "Export is not implemented yet";
      this.errorReporter.error_open = true;
    }
  }

  onDelete(): void {
    console.log("onDelete invoked");
    if (this.selected == null || this.selected.length == 0) {
      this.errorReporter.error_message = "Please select one or more items";
      this.errorReporter.error_open = true;
    } else {
      this.delete_open = true;
    }
  }

  onDeleteConfirmed(): void {
    console.log("onDeleteConfirmed invoked");
    var component = this;
    this.hostService.deleteHosts(this.selected)
      .then(function() {
        component.getHosts();
      });
    this.delete_open = false;
  }
}
