package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.HostApiService;
import io.goldfin.admin.service.api.service.impl.HostApiServiceImpl;


public class HostApiServiceFactory {
    private final static HostApiService service = new HostApiServiceImpl();

    public static HostApiService getHostApi() {
        return service;
    }
}
