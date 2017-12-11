package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.TenantApiService;
import io.goldfin.admin.service.api.service.impl.TenantApiServiceImpl;


public class TenantApiServiceFactory {
    private final static TenantApiService service = new TenantApiServiceImpl();

    public static TenantApiService getTenantApi() {
        return service;
    }
}
