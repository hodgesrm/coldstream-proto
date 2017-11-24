/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';

export class Host {
  identifier: string;
  vendor: string;
  name: string;
  type: string;
  region: string;
  datacenter: string;
  os: string;
  start_date: string;
  duration: number;
  cost: number;
  hourly_cost: number;
  daily_cost: number;
  weekly_cost: number;
  monthly_cost: number;
  currency: string;
}

@Injectable()
export class HostService {
  // In-memory hosts and host aggregates. 
  hosts: Host[] = null;

  getHosts(): Host[] {
    if (this.hosts === null) {
      console.log("Initializing hosts");
      this.hosts = this._fetchHosts();
      console.log("Initialized hosts: length=" + this.hosts.length);
    }
    return this.hosts;
  }

  // Generate hosts. 
  _fetchHosts(): Host[] {
    // Generates hosts locally for now. 
    console.log("Generating hosts");
    let hostGenerator = function(identifierPrefix, amount, vendor) {
      // Generate hosts with up to 20% variation in amount. 
      var hosts: Host[] = [];
      var i;
      for (i = 0; i < 25; i++) {
        var host = new Host();
        host.vendor = vendor;
        if (host.vendor === "AWS") {
          host.type = "CLOUD";
          host.identifier = "i-05c83264172013a" + i;
          host.region = "us-west";
          host.datacenter = "us-west-2a";
        } else {
          host.type = "DEDICATED";
          host.identifier = identifierPrefix + i; 
          host.region = "North America"
          host.datacenter = "BHS-1";
        }
        host.name = host.identifier;
        host.cost = amount;
        if (i % 2 == 0) {
          host.os = "Windows 10";
        } else {
          host.os = "RHEL 7";
        }
        host.start_date = "2017-10-01";
        host.duration = 2678400;
        host.hourly_cost = _roundCurrency(host.cost * 3600 / host.duration);
        host.daily_cost = _roundCurrency(host.cost * 3600 * 24 / host.duration);
        host.weekly_cost = _roundCurrency(host.cost * 3600 * 24 * 7/ host.duration);
        host.monthly_cost = _roundCurrency(host.cost * 3600 * 24 * 30 / host.duration);
        host.currency = "USD"
        host.currency = 'USD';

        hosts.push(host);
      }
      return hosts;
    }

    // Generate on a per vendor basis.  
    var vendorParams = [
      {prefix: "192.168.5.", amount: 69.99, vendor: "OVH.com"}, 
      {prefix: "prod-00", amount: 80.00, vendor: "Internap Corporation"},
      {prefix: "", amount: 90.00, vendor: "AWS"}
    ];
 
    var allHosts: Host[] = [];
    for (let params of vendorParams) {
      for (let vendorHosts of hostGenerator(params.prefix, 
          params.amount, params.vendor)) {
        allHosts = allHosts.concat(vendorHosts);
        console.log("Total hosts size=" + allHosts.length);
      }
    }
    return allHosts;
  }
}

function _roundCurrency(amount: number): number {
  return (Math.round(amount * 100)) / 100;
}
