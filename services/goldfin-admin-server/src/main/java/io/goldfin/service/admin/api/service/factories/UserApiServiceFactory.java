package io.goldfin.service.admin.api.service.factories;

import io.goldfin.service.admin.api.service.UserApiService;
import io.goldfin.service.admin.api.service.impl.UserApiServiceImpl;


public class UserApiServiceFactory {
    private final static UserApiService service = new UserApiServiceImpl();

    public static UserApiService getUserApi() {
        return service;
    }
}
