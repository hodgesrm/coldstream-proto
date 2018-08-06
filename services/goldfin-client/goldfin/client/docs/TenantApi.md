# api.TenantApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**tenant_create**](TenantApi.md#tenant_create) | **POST** /tenant | Create a new tenant
[**tenant_delete**](TenantApi.md#tenant_delete) | **DELETE** /tenant/{id} | Delete a tenant
[**tenant_show**](TenantApi.md#tenant_show) | **GET** /tenant/{id} | Show a single tenant
[**tenant_showall**](TenantApi.md#tenant_showall) | **GET** /tenant | List tenants
[**tenant_update**](TenantApi.md#tenant_update) | **PUT** /tenant/{id} | Update a tenant
[**user_create**](TenantApi.md#user_create) | **POST** /user | Create a new user for a tenant
[**user_delete**](TenantApi.md#user_delete) | **DELETE** /user/{id} | Delete a user
[**user_show**](TenantApi.md#user_show) | **GET** /user/{id} | Show a single user
[**user_showall**](TenantApi.md#user_showall) | **GET** /user | List users
[**user_update**](TenantApi.md#user_update) | **PUT** /user/{id} | Update a user
[**user_update_password**](TenantApi.md#user_update_password) | **PUT** /user/{id}/password | Update user password


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

# Configure API key authorization: APIKeyHeader
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

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
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

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
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

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
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

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
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

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_create**
> User user_create(body)

Create a new user for a tenant

Upload a new user registration request.

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
body = api.UserParameters() # UserParameters | User registration request parameters

try: 
    # Create a new user for a tenant
    api_response = api_instance.user_create(body)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling TenantApi->user_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**UserParameters**](UserParameters.md)| User registration request parameters | 

### Return type

[**User**](User.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_delete**
> user_delete(id)

Delete a user

Delete a user

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | User ID

try: 
    # Delete a user
    api_instance.user_delete(id)
except ApiException as e:
    print("Exception when calling TenantApi->user_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_show**
> Tenant user_show(id)

Show a single user

Return all information relative to a single user

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | User ID

try: 
    # Show a single user
    api_response = api_instance.user_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling TenantApi->user_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 

### Return type

[**Tenant**](Tenant.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_showall**
> list[User] user_showall()

List users

Return a list of all users visible to current user

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()

try: 
    # List users
    api_response = api_instance.user_showall()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling TenantApi->user_showall: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[User]**](User.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_update**
> user_update(id, body=body)

Update a user

Update user description

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | User ID
body = api.UserParameters() # UserParameters | User parameters (optional)

try: 
    # Update a user
    api_instance.user_update(id, body=body)
except ApiException as e:
    print("Exception when calling TenantApi->user_update: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **body** | [**UserParameters**](UserParameters.md)| User parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_update_password**
> user_update_password(id, body=body)

Update user password

Sets a new user password

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.TenantApi()
id = 'id_example' # str | User ID
body = api.UserPasswordParameters() # UserPasswordParameters | Password change parameters (optional)

try: 
    # Update user password
    api_instance.user_update_password(id, body=body)
except ApiException as e:
    print("Exception when calling TenantApi->user_update_password: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **body** | [**UserPasswordParameters**](UserPasswordParameters.md)| Password change parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

