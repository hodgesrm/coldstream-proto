package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.HostApiService;
import io.goldfin.front.invoice.api.service.impl.HostApiServiceImpl;


public class HostApiServiceFactory {
    private final static HostApiService service = new HostApiServiceImpl();

    public static HostApiService getHostApi() {
        return service;
    }
}
