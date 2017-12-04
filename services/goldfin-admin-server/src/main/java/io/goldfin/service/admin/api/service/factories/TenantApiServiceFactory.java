package io.goldfin.service.admin.api.service.factories;

import io.goldfin.service.admin.api.service.TenantApiService;
import io.goldfin.service.admin.api.service.impl.TenantApiServiceImpl;


public class TenantApiServiceFactory {
    private final static TenantApiService service = new TenantApiServiceImpl();

    public static TenantApiService getTenantApi() {
        return service;
    }
}
