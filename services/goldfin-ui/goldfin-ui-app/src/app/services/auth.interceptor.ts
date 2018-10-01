/*
 * Copyright (c) 2017-2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { HttpResponse, HttpErrorResponse, HttpHandler, HttpInterceptor, 
         HttpRequest 
       } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { finalize, tap } from 'rxjs/operators';

import { Router } from '@angular/router';

// Intercepts authorization errors on HTTP and redirects
// to login screen unless the request is from the login process
// itself.
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    let status: string;
    // extend server response observable with logging
    return next.handle(req)
      .pipe(
        tap(
          // Succeeds when there is a response; ignore other events
          event => status = event instanceof HttpResponse ? 'succeeded' : '',
          // Operation failed; error is an HttpErrorResponse
          error => {
            console.log(error);
            // Check for an authentication failure.  We permit these go 
            // through if we are logging in.  However an auth failure on 
            // any other operations means we should route to the login 
            // screen. 
            if (error.status == 401) {
              if (req.method == "POST" && 
                  req.url.match("\/session$") != null) {
                status = 'login failure';
              } else {
                status = 'auth needed';
              }
            } 
          }
        ),
        // When response observable either completes or errors out check
        // for an unexpected auth failure and redirect to login screen. 
        finalize(() => {
          // const msg = `${req.method} "${req.urlWithParams}" ${status}`;
          // console.log(msg);
          if (status == 'auth needed') {
            console.log('Auth failure; redirecting to login page');
            this.router.navigate(['login']);
          }
        })
      );
  }
}
