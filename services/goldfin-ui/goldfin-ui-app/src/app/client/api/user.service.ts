/**
 * Goldfin Service API
 * REST API for Goldfin Intelligent Invoice Processing
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@goldfin.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs/Observable';

import { ApiKey } from '../model/apiKey';
import { ApiKeyParameters } from '../model/apiKeyParameters';
import { ApiResponse } from '../model/apiResponse';
import { Tenant } from '../model/tenant';
import { User } from '../model/user';
import { UserParameters } from '../model/userParameters';
import { UserPasswordParameters } from '../model/userPasswordParameters';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class UserService {

    protected basePath = 'https://api.goldfin.io/api/v1';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (let consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }


    /**
     * Create a new API key
     * Create a new API key that can be used for application access.
     * @param id User ID
     * @param body API Key parameters, including name of key.
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public apikeyCreate(id: string, body?: ApiKeyParameters, observe?: 'body', reportProgress?: boolean): Observable<ApiKey>;
    public apikeyCreate(id: string, body?: ApiKeyParameters, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ApiKey>>;
    public apikeyCreate(id: string, body?: ApiKeyParameters, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ApiKey>>;
    public apikeyCreate(id: string, body?: ApiKeyParameters, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling apikeyCreate.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.post<ApiKey>(`${this.basePath}/user/${encodeURIComponent(String(id))}/apikey`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Delete an API key
     * Deletes API key.  Any applications using the key will no longer function.
     * @param id User ID
     * @param keyid API Key ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public apikeyDelete(id: string, keyid: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public apikeyDelete(id: string, keyid: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public apikeyDelete(id: string, keyid: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public apikeyDelete(id: string, keyid: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling apikeyDelete.');
        }
        if (keyid === null || keyid === undefined) {
            throw new Error('Required parameter keyid was null or undefined when calling apikeyDelete.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.delete<any>(`${this.basePath}/user/${encodeURIComponent(String(id))}/apikey/${encodeURIComponent(String(keyid))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Return list of API keys
     * Return a list of API keys.  Secrets are not shown.
     * @param id User ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public apikeyShowAll(id: string, observe?: 'body', reportProgress?: boolean): Observable<Array<ApiKey>>;
    public apikeyShowAll(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ApiKey>>>;
    public apikeyShowAll(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ApiKey>>>;
    public apikeyShowAll(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling apikeyShowAll.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<Array<ApiKey>>(`${this.basePath}/user/${encodeURIComponent(String(id))}/apikey`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Create a new user for a tenant
     * Upload a new user registration request.
     * @param body User registration request parameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userCreate(body: UserParameters, observe?: 'body', reportProgress?: boolean): Observable<User>;
    public userCreate(body: UserParameters, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<User>>;
    public userCreate(body: UserParameters, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<User>>;
    public userCreate(body: UserParameters, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (body === null || body === undefined) {
            throw new Error('Required parameter body was null or undefined when calling userCreate.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.post<User>(`${this.basePath}/user`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Delete a user
     * Delete a user
     * @param id User ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userDelete(id: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public userDelete(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public userDelete(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public userDelete(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling userDelete.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.delete<any>(`${this.basePath}/user/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Show a single user
     * Return all information relative to a single user
     * @param id User ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userShow(id: string, observe?: 'body', reportProgress?: boolean): Observable<Tenant>;
    public userShow(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Tenant>>;
    public userShow(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Tenant>>;
    public userShow(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling userShow.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<Tenant>(`${this.basePath}/user/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * List users
     * Return a list of all users visible to current user
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userShowall(observe?: 'body', reportProgress?: boolean): Observable<Array<User>>;
    public userShowall(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<User>>>;
    public userShowall(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<User>>>;
    public userShowall(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<Array<User>>(`${this.basePath}/user`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Update a user
     * Update user description
     * @param id User ID
     * @param body User parameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userUpdate(id: string, body?: UserParameters, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public userUpdate(id: string, body?: UserParameters, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public userUpdate(id: string, body?: UserParameters, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public userUpdate(id: string, body?: UserParameters, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling userUpdate.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.put<any>(`${this.basePath}/user/${encodeURIComponent(String(id))}`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Update user password
     * Sets a new user password
     * @param id User ID
     * @param body Password change parameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public userUpdatePassword(id: string, body?: UserPasswordParameters, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public userUpdatePassword(id: string, body?: UserPasswordParameters, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public userUpdatePassword(id: string, body?: UserPasswordParameters, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public userUpdatePassword(id: string, body?: UserPasswordParameters, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling userUpdatePassword.');
        }

        let headers = this.defaultHeaders;

        // authentication (ApiKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.apikey"]) {
            headers = headers.set('vnd.io.goldfin.apikey', this.configuration.apiKeys["vnd.io.goldfin.apikey"]);
        }

        // authentication (SessionKey) required
        if (this.configuration.apiKeys["vnd.io.goldfin.session"]) {
            headers = headers.set('vnd.io.goldfin.session', this.configuration.apiKeys["vnd.io.goldfin.session"]);
        }

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.put<any>(`${this.basePath}/user/${encodeURIComponent(String(id))}/password`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
