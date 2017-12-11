package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.UserApiService;
import io.goldfin.admin.service.api.service.impl.UserApiServiceImpl;


public class UserApiServiceFactory {
    private final static UserApiService service = new UserApiServiceImpl();

    public static UserApiService getUserApi() {
        return service;
    }
}
