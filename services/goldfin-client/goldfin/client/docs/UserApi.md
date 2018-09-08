# api.UserApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**apikey_create**](UserApi.md#apikey_create) | **POST** /user/{id}/apikey | Create a new API key
[**apikey_delete**](UserApi.md#apikey_delete) | **DELETE** /user/{id}/apikey/{keyid} | Delete an API key
[**apikey_show_all**](UserApi.md#apikey_show_all) | **GET** /user/{id}/apikey | Return list of API keys
[**user_create**](UserApi.md#user_create) | **POST** /user | Create a new user for a tenant
[**user_delete**](UserApi.md#user_delete) | **DELETE** /user/{id} | Delete a user
[**user_show**](UserApi.md#user_show) | **GET** /user/{id} | Show a single user
[**user_showall**](UserApi.md#user_showall) | **GET** /user | List users
[**user_update**](UserApi.md#user_update) | **PUT** /user/{id} | Update a user
[**user_update_password**](UserApi.md#user_update_password) | **PUT** /user/{id}/password | Update user password


# **apikey_create**
> ApiKey apikey_create(id, body=body)

Create a new API key

Create a new API key that can be used for application access.

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
api_instance = api.UserApi()
id = 'id_example' # str | User ID
body = api.ApiKeyParameters() # ApiKeyParameters | API Key parameters, including name of key. (optional)

try: 
    # Create a new API key
    api_response = api_instance.apikey_create(id, body=body)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UserApi->apikey_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **body** | [**ApiKeyParameters**](ApiKeyParameters.md)| API Key parameters, including name of key. | [optional] 

### Return type

[**ApiKey**](ApiKey.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apikey_delete**
> apikey_delete(id, keyid)

Delete an API key

Deletes API key.  Any applications using the key will no longer function.

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
api_instance = api.UserApi()
id = 'id_example' # str | User ID
keyid = 'keyid_example' # str | API Key ID

try: 
    # Delete an API key
    api_instance.apikey_delete(id, keyid)
except ApiException as e:
    print("Exception when calling UserApi->apikey_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **keyid** | **str**| API Key ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apikey_show_all**
> list[ApiKey] apikey_show_all(id)

Return list of API keys

Return a list of API keys.  Secrets are not shown.

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
api_instance = api.UserApi()
id = 'id_example' # str | User ID

try: 
    # Return list of API keys
    api_response = api_instance.apikey_show_all(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UserApi->apikey_show_all: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 

### Return type

[**list[ApiKey]**](ApiKey.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()
body = api.UserParameters() # UserParameters | User registration request parameters

try: 
    # Create a new user for a tenant
    api_response = api_instance.user_create(body)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UserApi->user_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**UserParameters**](UserParameters.md)| User registration request parameters | 

### Return type

[**User**](User.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()
id = 'id_example' # str | User ID

try: 
    # Delete a user
    api_instance.user_delete(id)
except ApiException as e:
    print("Exception when calling UserApi->user_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()
id = 'id_example' # str | User ID

try: 
    # Show a single user
    api_response = api_instance.user_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UserApi->user_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 

### Return type

[**Tenant**](Tenant.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()

try: 
    # List users
    api_response = api_instance.user_showall()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UserApi->user_showall: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[User]**](User.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()
id = 'id_example' # str | User ID
body = api.UserParameters() # UserParameters | User parameters (optional)

try: 
    # Update a user
    api_instance.user_update(id, body=body)
except ApiException as e:
    print("Exception when calling UserApi->user_update: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **body** | [**UserParameters**](UserParameters.md)| User parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

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

# Configure API key authorization: ApiKey
api.configuration.api_key['vnd.io.goldfin.apikey'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.apikey'] = 'Bearer'
# Configure API key authorization: SessionKey
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.UserApi()
id = 'id_example' # str | User ID
body = api.UserPasswordParameters() # UserPasswordParameters | Password change parameters (optional)

try: 
    # Update user password
    api_instance.user_update_password(id, body=body)
except ApiException as e:
    print("Exception when calling UserApi->user_update_password: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| User ID | 
 **body** | [**UserPasswordParameters**](UserPasswordParameters.md)| Password change parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

