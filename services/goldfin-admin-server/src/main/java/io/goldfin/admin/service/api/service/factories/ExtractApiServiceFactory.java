package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.ExtractApiService;
import io.goldfin.admin.service.api.service.impl.ExtractApiServiceImpl;


public class ExtractApiServiceFactory {
    private final static ExtractApiService service = new ExtractApiServiceImpl();

    public static ExtractApiService getExtractApi() {
        return service;
    }
}
