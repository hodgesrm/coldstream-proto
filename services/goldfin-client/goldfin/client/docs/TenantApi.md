# api.TenantApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**tenant_create**](TenantApi.md#tenant_create) | **POST** /tenant | Create a new tenant
[**tenant_delete**](TenantApi.md#tenant_delete) | **DELETE** /tenant/{id} | Delete a tenant
[**tenant_show**](TenantApi.md#tenant_show) | **GET** /tenant/{id} | Show a single tenant
[**tenant_showall**](TenantApi.md#tenant_showall) | **GET** /tenant | List tenants
[**tenant_update**](TenantApi.md#tenant_update) | **PUT** /tenant/{id} | Update a tenant


# **tenant_create**
> Tenant tenant_create(body)

Create a new tenant

Upload a new tenant registration request.

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
body = api.TenantParameters() # TenantParameters | Tenant creation parameters

try: 
    # Create a new tenant
    api_response = api_instance.tenant_create(body)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling TenantApi->tenant_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**TenantParameters**](TenantParameters.md)| Tenant creation parameters | 

### Return type

[**Tenant**](Tenant.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **tenant_delete**
> tenant_delete(id)

Delete a tenant

Delete a single tenant

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | Tenant ID

try: 
    # Delete a tenant
    api_instance.tenant_delete(id)
except ApiException as e:
    print("Exception when calling TenantApi->tenant_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Tenant ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **tenant_show**
> Tenant tenant_show(id)

Show a single tenant

Return all information relative to a single tenant

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | Tenant ID

try: 
    # Show a single tenant
    api_response = api_instance.tenant_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling TenantApi->tenant_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Tenant ID | 

### Return type

[**Tenant**](Tenant.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **tenant_showall**
> tenant_showall()

List tenants

Return a list of all tenants

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()

try: 
    # List tenants
    api_instance.tenant_showall()
except ApiException as e:
    print("Exception when calling TenantApi->tenant_showall: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **tenant_update**
> tenant_update(id, body=body)

Update a tenant

Update invoice description and tags. Changes to other fields are ignored

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | Tenant ID
body = api.TenantParameters() # TenantParameters | Tenant parameters (optional)

try: 
    # Update a tenant
    api_instance.tenant_update(id, body=body)
except ApiException as e:
    print("Exception when calling TenantApi->tenant_update: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Tenant ID | 
 **body** | [**TenantParameters**](TenantParameters.md)| Tenant parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

