# api.InvoiceApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**invoice_delete**](InvoiceApi.md#invoice_delete) | **DELETE** /invoice/{id} | Delete an invoice
[**invoice_download**](InvoiceApi.md#invoice_download) | **GET** /invoice/{id}/download | Download invoice document
[**invoice_show**](InvoiceApi.md#invoice_show) | **GET** /invoice/{id} | Show a single invoice
[**invoice_show_all**](InvoiceApi.md#invoice_show_all) | **GET** /invoice | List invoices
[**invoice_validate**](InvoiceApi.md#invoice_validate) | **POST** /invoice/{id}/validate | Start invoice validations


# **invoice_delete**
> invoice_delete(id)

Delete an invoice

Delete an invoice.  It can be recreated by rescanning the corresponding document

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
api_instance = api.InvoiceApi()
id = 'id_example' # str | Invoice ID

try: 
    # Delete an invoice
    api_instance.invoice_delete(id)
except ApiException as e:
    print("Exception when calling InvoiceApi->invoice_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Invoice ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **invoice_download**
> file invoice_download(id)

Download invoice document

Download invoice document

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
api_instance = api.InvoiceApi()
id = 'id_example' # str | Document ID

try: 
    # Download invoice document
    api_response = api_instance.invoice_download(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InvoiceApi->invoice_download: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Document ID | 

### Return type

[**file**](file.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf, application/octet-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **invoice_show**
> Invoice invoice_show(id, full=full)

Show a single invoice

Return all information relative to a single invoice

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
api_instance = api.InvoiceApi()
id = 'id_example' # str | Invoice ID
full = true # bool | If true, return full invoices with all line items (optional)

try: 
    # Show a single invoice
    api_response = api_instance.invoice_show(id, full=full)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InvoiceApi->invoice_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Invoice ID | 
 **full** | **bool**| If true, return full invoices with all line items | [optional] 

### Return type

[**Invoice**](Invoice.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **invoice_show_all**
> list[Invoice] invoice_show_all(full=full)

List invoices

Return a list of all invoices

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
api_instance = api.InvoiceApi()
full = true # bool | If true, return full invoices with all line items (optional)

try: 
    # List invoices
    api_response = api_instance.invoice_show_all(full=full)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InvoiceApi->invoice_show_all: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **full** | **bool**| If true, return full invoices with all line items | [optional] 

### Return type

[**list[Invoice]**](Invoice.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **invoice_validate**
> list[InvoiceValidationResult] invoice_validate(id, only_failing=only_failing)

Start invoice validations

Run invoice validations

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
api_instance = api.InvoiceApi()
id = 'id_example' # str | Invoice ID
only_failing = false # bool | If true, return only failing checks. Otherwise return all results including checks that succeed (optional) (default to false)

try: 
    # Start invoice validations
    api_response = api_instance.invoice_validate(id, only_failing=only_failing)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InvoiceApi->invoice_validate: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Invoice ID | 
 **only_failing** | **bool**| If true, return only failing checks. Otherwise return all results including checks that succeed | [optional] [default to false]

### Return type

[**list[InvoiceValidationResult]**](InvoiceValidationResult.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

