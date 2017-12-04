package io.goldfin.service.admin.api.service.factories;

import io.goldfin.service.admin.api.service.LoginApiService;
import io.goldfin.service.admin.api.service.impl.LoginApiServiceImpl;


public class LoginApiServiceFactory {
    private final static LoginApiService service = new LoginApiServiceImpl();

    public static LoginApiService getLoginApi() {
        return service;
    }
}
