package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.InvoiceApiService;
import io.goldfin.admin.service.api.service.impl.InvoiceApiServiceImpl;


public class InvoiceApiServiceFactory {
    private final static InvoiceApiService service = new InvoiceApiServiceImpl();

    public static InvoiceApiService getInvoiceApi() {
        return service;
    }
}
