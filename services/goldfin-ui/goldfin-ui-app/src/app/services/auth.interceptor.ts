/*
 * Copyright (c) 2017-2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import { Router } from '@angular/router';

// Intercepts authorization errors on HTTP in a general way.  This is 
// taken from an example on Stack Overflow. 
// (https://stackoverflow.com/questions/46017245/how-to-handle-unauthorized-requestsstatus-with-401-or-403-with-new-httpclient)
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private router: Router) { }

    // Whenever we get an auth error redirect to login. Others can be
    // rethrown and processed normally. 
    private handleAuthError(err: HttpErrorResponse): Observable<any> {
        if (err.status === 401 || err.status === 403) {
            this.router.navigateByUrl(`/login`);
            return Observable.of(err.message);
        }
        return Observable.throw(err);
    }

    // Implements the interceptor. 
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // Clone the request to add the new header.
        // const authReq = req.clone({headers: req.headers.set(Cookie.tokenKey, Cookie.getToken())});
        // catch the error, make specific functions for catching specific 
        // errors and you can chain through them with more catch operators
        return next.handle(req).catch(x=> this.handleAuthError(x)); 
        //here use an arrow function, otherwise you may get 
        // "Cannot read property 'navigate' of undefined" on 
        //angular 4.4.2/net core 2/webpack 2.70
    }
}
