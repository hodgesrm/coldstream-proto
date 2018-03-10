/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable, OnInit } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';

import { AuthService } from './auth.service';

@Injectable()
export class AuthGuardService implements CanActivate {
  constructor(
    public auth: AuthService, 
    public router: Router
  ) {}

  // Send to login if not authorized. 
  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (this.auth.isAuthorized()) {
      return true;
    } else {
      this.router.navigate(['login']);
      return false;
    }
  }
}
