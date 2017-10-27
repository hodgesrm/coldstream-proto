package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.TenantApiService;
import io.goldfin.front.invoice.api.service.impl.TenantApiServiceImpl;


public class TenantApiServiceFactory {
    private final static TenantApiService service = new TenantApiServiceImpl();

    public static TenantApiService getTenantApi() {
        return service;
    }
}
