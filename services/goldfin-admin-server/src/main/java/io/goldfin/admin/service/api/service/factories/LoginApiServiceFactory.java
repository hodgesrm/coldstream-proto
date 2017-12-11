package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.LoginApiService;
import io.goldfin.admin.service.api.service.impl.LoginApiServiceImpl;


public class LoginApiServiceFactory {
    private final static LoginApiService service = new LoginApiServiceImpl();

    public static LoginApiService getLoginApi() {
        return service;
    }
}
