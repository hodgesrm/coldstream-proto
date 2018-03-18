/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */

import { Inject, Injectable, Optional } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { RequestMethod, RequestOptions, RequestOptionsArgs } from '@angular/http';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';

import { DocumentApi } from '../client/api/DocumentApi';
import * as models from '../client/model/models';
import { BASE_PATH, COLLECTION_FORMATS } from '../client/variables';
import { Configuration } from '../client/configuration';

// Subclassed Document API to implement form-based document loading. 
@Injectable()
export class DocumentApiExtended extends DocumentApi {

    constructor(protected http: Http, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        super(http, basePath, configuration);
    }

    /**
     * Upload document.  This is tweaked to generate binary form data 
     * correctly, as the swagger-codegen implementation code tries to 
     * load data as URL-encoded search parameters. 
     * @param file Document file
     * @param description A optional description of the document
     * @param scan Flag to control scanning
     */
    public documentCreateWithHttpInfo(file: any, description?: string, scan?: boolean, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + '/document';

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        let formParams = new FormData();

        // verify required parameter 'file' is not null or undefined
        if (file === null || file === undefined) {
            throw new Error('Required parameter file was null or undefined when calling documentCreate.');
        }
        // to determine the Content-Type header
        let consumes: string[] = [
            'multipart/form-data'
        ];

        // to determine the Accept header
        let produces: string[] = [
            'application/json'
        ];

        // authentication (APIKeyHeader) required
        if (this.configuration.apiKey) {
            headers.set('vnd.io.goldfin.session', this.configuration.apiKey);
        }

        // Do not set content type.  We let the Http implementation do this. 
        // (It should be form data.)

        if (description !== undefined) {
            formParams.append('description', <any>description);
        }

        if (scan !== undefined) {
            formParams.append('scan', <any>scan);
        }

        if (file !== undefined) {
            formParams.append('file', file, file.name);
        }

        let requestOptions: RequestOptionsArgs = new RequestOptions({
            method: RequestMethod.Post,
            headers: headers,
            body: formParams,
            search: queryParameters,
            withCredentials:this.configuration.withCredentials
        });
        // https://github.com/swagger-api/swagger-codegen/issues/4037
        if (extraHttpRequestParams) {
            requestOptions = (<any>Object).assign(requestOptions, extraHttpRequestParams);
        }

        return this.http.request(path, requestOptions);
    }
}
