/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable, OnInit } from '@angular/core';
import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

import { User } from './user';

import { Configuration } from '../client/configuration';
import { SecurityApi } from '../client/api/SecurityApi';
import { LoginCredentials } from '../client/model/LoginCredentials';

class Session {
  userName: string;
  apiKey: string;
}

@Injectable()
export class AuthService {
  // Should store the apiKey here so that components
  // can easily check if we are logged in. 

  constructor(
    private configuration: Configuration,
    private securityApi: SecurityApi
  ) {}

  // Return true if we are authorized.   
  isAuthorized(): boolean {
    return (this.configuration.apiKey != null);
  }

  // Look up user/password on server.
  authorize(name: string, password: string): Observable<{}> {
    console.log("Authenticating user: " + name);
    const loginCredential = {
      user: name, password: password
    };
    var request: Observable<{}>;
    request = this.securityApi.loginByCredentialsWithHttpInfo(loginCredential)
      .map((response: Response) => {
        // Map 200 response to a session. 
        var apiKey = response.headers.get("vnd.io.goldfin.session");
        console.log("Status: " + response.status);
        console.log("Api Key: " + apiKey);
        var session: Session;
        if (apiKey == null) {
          session = null;
        } else {
          // Set apiKey in the configuration where it will be used by 
          // later calls. 
          this.configuration.apiKey = apiKey;
          session = { userName: name, apiKey: apiKey };
        }
        return session;
      })
      .catch((err) => {
        // Convert any non-200 response (including client errors) to 
        // an appropriate error message. 
        console.log(err);
        if (err.status == 401) {
          throw new Error("Invalid user name or password");
        } else {
          throw new Error("Login attempt failed");
        }
      });
    return request;
  }
}
