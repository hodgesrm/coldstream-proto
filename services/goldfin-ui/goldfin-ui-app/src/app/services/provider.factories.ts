/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthInterceptor } from './auth.interceptor';

// Define factory method to return API base URL. 
export let baseUrlFactory = () => {
  // The native Javascript window object knows the origin of the 
  // current document.  We decide whether to use it or https://localhost:8443 
  // value depending on whether we are running in prod or dev.
  let origin = window.location.origin;
  let baseUrl = undefined;
  if (environment.production) {
    baseUrl = origin + "/api/v1";
  } else {
    baseUrl = "https://localhost:8443/api/v1";
  }    
  console.log("Base API URL: " + baseUrl);
  return baseUrl;
};

// Define factory method to return HTTP interceptor
export let httpInterceptorFactory = (router: Router) => {
  return new AuthInterceptor(router);
};
