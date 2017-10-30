package io.goldfin.front.invoice.api.service.factories;

import io.goldfin.front.invoice.api.service.LoginApiService;
import io.goldfin.front.invoice.api.service.impl.LoginApiServiceImpl;


public class LoginApiServiceFactory {
    private final static LoginApiService service = new LoginApiServiceImpl();

    public static LoginApiService getLoginApi() {
        return service;
    }
}
