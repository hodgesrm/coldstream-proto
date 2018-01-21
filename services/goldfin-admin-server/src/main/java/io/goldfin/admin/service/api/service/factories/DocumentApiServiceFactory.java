package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.DocumentApiService;
import io.goldfin.admin.service.api.service.impl.DocumentApiServiceImpl;


public class DocumentApiServiceFactory {
    private final static DocumentApiService service = new DocumentApiServiceImpl();

    public static DocumentApiService getDocumentApi() {
        return service;
    }
}
