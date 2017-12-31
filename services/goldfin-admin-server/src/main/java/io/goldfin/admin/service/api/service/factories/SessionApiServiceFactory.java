package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.SessionApiService;
import io.goldfin.admin.service.api.service.impl.SessionApiServiceImpl;


public class SessionApiServiceFactory {
    private final static SessionApiService service = new SessionApiServiceImpl();

    public static SessionApiService getSessionApi() {
        return service;
    }
}
