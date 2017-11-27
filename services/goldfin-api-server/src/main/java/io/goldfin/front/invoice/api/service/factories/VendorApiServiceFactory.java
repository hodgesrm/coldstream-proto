package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.VendorApiService;
import io.goldfin.front.invoice.api.service.impl.VendorApiServiceImpl;


public class VendorApiServiceFactory {
    private final static VendorApiService service = new VendorApiServiceImpl();

    public static VendorApiService getVendorApi() {
        return service;
    }
}
