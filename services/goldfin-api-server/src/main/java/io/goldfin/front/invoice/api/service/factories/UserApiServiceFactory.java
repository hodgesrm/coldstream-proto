package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.UserApiService;
import io.goldfin.front.invoice.api.service.impl.UserApiServiceImpl;


public class UserApiServiceFactory {
    private final static UserApiService service = new UserApiServiceImpl();

    public static UserApiService getUserApi() {
        return service;
    }
}
