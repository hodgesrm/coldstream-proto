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

import { Invoice } from '../model/invoice';
import { InvoiceValidationResult } from '../model/invoiceValidationResult';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class InvoiceService {

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
     * Delete an invoice
     * Delete an invoice.  It can be recreated by rescanning the corresponding document
     * @param id Invoice ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public invoiceDelete(id: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public invoiceDelete(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public invoiceDelete(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public invoiceDelete(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling invoiceDelete.');
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

        return this.httpClient.delete<any>(`${this.basePath}/invoice/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Download invoice document
     * Download invoice document
     * @param id Document ID
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public invoiceDownload(id: string, observe?: 'body', reportProgress?: boolean): Observable<Blob>;
    public invoiceDownload(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Blob>>;
    public invoiceDownload(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Blob>>;
    public invoiceDownload(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling invoiceDownload.');
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
            'application/pdf',
            'application/octet-stream'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get(`${this.basePath}/invoice/${encodeURIComponent(String(id))}/download`,
            {
                responseType: "blob",
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Show a single invoice
     * Return all information relative to a single invoice
     * @param id Invoice ID
     * @param full If true, return full invoices with all line items
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public invoiceShow(id: string, full?: boolean, observe?: 'body', reportProgress?: boolean): Observable<Invoice>;
    public invoiceShow(id: string, full?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Invoice>>;
    public invoiceShow(id: string, full?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Invoice>>;
    public invoiceShow(id: string, full?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling invoiceShow.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (full !== undefined) {
            queryParameters = queryParameters.set('full', <any>full);
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

        return this.httpClient.get<Invoice>(`${this.basePath}/invoice/${encodeURIComponent(String(id))}`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * List invoices
     * Return a list of all invoices
     * @param full If true, return full invoices with all line items
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public invoiceShowAll(full?: boolean, observe?: 'body', reportProgress?: boolean): Observable<Array<Invoice>>;
    public invoiceShowAll(full?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<Invoice>>>;
    public invoiceShowAll(full?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<Invoice>>>;
    public invoiceShowAll(full?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (full !== undefined) {
            queryParameters = queryParameters.set('full', <any>full);
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

        return this.httpClient.get<Array<Invoice>>(`${this.basePath}/invoice`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Start invoice validations
     * Run invoice validations
     * @param id Invoice ID
     * @param onlyFailing If true, return only failing checks. Otherwise return all results including checks that succeed
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public invoiceValidate(id: string, onlyFailing?: boolean, observe?: 'body', reportProgress?: boolean): Observable<Array<InvoiceValidationResult>>;
    public invoiceValidate(id: string, onlyFailing?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<InvoiceValidationResult>>>;
    public invoiceValidate(id: string, onlyFailing?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<InvoiceValidationResult>>>;
    public invoiceValidate(id: string, onlyFailing?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling invoiceValidate.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (onlyFailing !== undefined) {
            queryParameters = queryParameters.set('onlyFailing', <any>onlyFailing);
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

        return this.httpClient.post<Array<InvoiceValidationResult>>(`${this.basePath}/invoice/${encodeURIComponent(String(id))}/validate`,
            null,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
