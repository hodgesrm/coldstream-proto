# api.VendorApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**vendor_create**](VendorApi.md#vendor_create) | **POST** /vendor | Create a new vendor
[**vendor_delete**](VendorApi.md#vendor_delete) | **DELETE** /vendor/{id} | Delete a vendor
[**vendor_show**](VendorApi.md#vendor_show) | **GET** /vendor/{id} | Show a single vendor
[**vendor_showall**](VendorApi.md#vendor_showall) | **GET** /vendor | List vendors
[**vendor_update**](VendorApi.md#vendor_update) | **PUT** /vendor/{id} | Update a vendor


# **vendor_create**
> Vendor vendor_create(body)

Create a new vendor

Upload a new vendor definition.  Vendors are also created automatically if a vendor invoice is processed.

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
api_instance = api.VendorApi()
body = api.VendorParameters() # VendorParameters | Vendor registration request parameters

try: 
    # Create a new vendor
    api_response = api_instance.vendor_create(body)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling VendorApi->vendor_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**VendorParameters**](VendorParameters.md)| Vendor registration request parameters | 

### Return type

[**Vendor**](Vendor.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **vendor_delete**
> vendor_delete(id)

Delete a vendor

Delete a single vendor.  This can only be done if the vendor is not attached to invoices or existing inventory.

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
api_instance = api.VendorApi()
id = 'id_example' # str | vendor ID

try: 
    # Delete a vendor
    api_instance.vendor_delete(id)
except ApiException as e:
    print("Exception when calling VendorApi->vendor_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| vendor ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **vendor_show**
> Vendor vendor_show(id)

Show a single vendor

Return all information relative to a single vendor

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
api_instance = api.VendorApi()
id = 'id_example' # str | Vendor ID

try: 
    # Show a single vendor
    api_response = api_instance.vendor_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling VendorApi->vendor_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Vendor ID | 

### Return type

[**Vendor**](Vendor.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **vendor_showall**
> vendor_showall()

List vendors

Return a list of all vendors

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
api_instance = api.VendorApi()

try: 
    # List vendors
    api_instance.vendor_showall()
except ApiException as e:
    print("Exception when calling VendorApi->vendor_showall: %s\n" % e)
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

# **vendor_update**
> vendor_update(id, body=body)

Update a vendor

Update vendor description.

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
api_instance = api.VendorApi()
id = 'id_example' # str | Vendor ID
body = api.VendorParameters() # VendorParameters | Vendor parameters (optional)

try: 
    # Update a vendor
    api_instance.vendor_update(id, body=body)
except ApiException as e:
    print("Exception when calling VendorApi->vendor_update: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Vendor ID | 
 **body** | [**VendorParameters**](VendorParameters.md)| Vendor parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

