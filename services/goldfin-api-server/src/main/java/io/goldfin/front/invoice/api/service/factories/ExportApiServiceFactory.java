package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.ExportApiService;
import io.goldfin.front.invoice.api.service.impl.ExportApiServiceImpl;


public class ExportApiServiceFactory {
    private final static ExportApiService service = new ExportApiServiceImpl();

    public static ExportApiService getExportApi() {
        return service;
    }
}
