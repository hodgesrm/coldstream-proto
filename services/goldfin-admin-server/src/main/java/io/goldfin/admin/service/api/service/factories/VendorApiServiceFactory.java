package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.VendorApiService;
import io.goldfin.admin.service.api.service.impl.VendorApiServiceImpl;


public class VendorApiServiceFactory {
    private final static VendorApiService service = new VendorApiServiceImpl();

    public static VendorApiService getVendorApi() {
        return service;
    }
}
