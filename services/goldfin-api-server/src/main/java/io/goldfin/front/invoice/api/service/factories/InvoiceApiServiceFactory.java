package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.InvoiceApiService;
import io.goldfin.front.invoice.api.service.impl.InvoiceApiServiceImpl;


public class InvoiceApiServiceFactory {
    private final static InvoiceApiService service = new InvoiceApiServiceImpl();

    public static InvoiceApiService getInvoiceApi() {
        return service;
    }
}
