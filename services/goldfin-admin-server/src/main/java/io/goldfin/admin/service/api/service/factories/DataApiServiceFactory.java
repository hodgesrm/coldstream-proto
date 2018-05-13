package io.goldfin.admin.service.api.service.factories;

import io.goldfin.admin.service.api.service.DataApiService;
import io.goldfin.admin.service.api.service.impl.DataApiServiceImpl;


public class DataApiServiceFactory {
    private final static DataApiService service = new DataApiServiceImpl();

    public static DataApiService getDataApi() {
        return service;
    }
}
