# api.DocumentApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**document_create**](DocumentApi.md#document_create) | **POST** /document | Upload document
[**document_delete**](DocumentApi.md#document_delete) | **DELETE** /document/{id} | Delete a invoice
[**document_download**](DocumentApi.md#document_download) | **GET** /document/{id}/download | Download content
[**document_process**](DocumentApi.md#document_process) | **POST** /document/{id}/process | Kick off document analysis
[**document_show**](DocumentApi.md#document_show) | **GET** /document/{id} | Return document metadata
[**document_show_all**](DocumentApi.md#document_show_all) | **GET** /document | List documents


# **document_create**
> Document document_create(file, description=description, process=process)

Upload document

Upload a new document for scanning

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
api_instance = api.DocumentApi()
file = '/path/to/file.txt' # file | Document file
description = 'description_example' # str | A optional description of the document (optional)
process = true # bool | Optional flag to kick off scanning automatically if true (optional) (default to true)

try: 
    # Upload document
    api_response = api_instance.document_create(file, description=description, process=process)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DocumentApi->document_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **file**| Document file | 
 **description** | **str**| A optional description of the document | [optional] 
 **process** | **bool**| Optional flag to kick off scanning automatically if true | [optional] [default to true]

### Return type

[**Document**](Document.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **document_delete**
> document_delete(id)

Delete a invoice

Delete a single document and associated semantic content

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
api_instance = api.DocumentApi()
id = 'id_example' # str | Invoice ID

try: 
    # Delete a invoice
    api_instance.document_delete(id)
except ApiException as e:
    print("Exception when calling DocumentApi->document_delete: %s\n" % e)
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

# **document_download**
> file document_download(id)

Download content

Download document content

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
api_instance = api.DocumentApi()
id = 'id_example' # str | Document ID

try: 
    # Download content
    api_response = api_instance.document_download(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DocumentApi->document_download: %s\n" % e)
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

# **document_process**
> document_process(id)

Kick off document analysis

Run background scanning on document.  The document state and semantic information will be updated when finished.

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
api_instance = api.DocumentApi()
id = 'id_example' # str | Document ID

try: 
    # Kick off document analysis
    api_instance.document_process(id)
except ApiException as e:
    print("Exception when calling DocumentApi->document_process: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Document ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **document_show**
> Document document_show(id)

Return document metadata

Download document metadata without content

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
api_instance = api.DocumentApi()
id = 'id_example' # str | Document ID

try: 
    # Return document metadata
    api_response = api_instance.document_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DocumentApi->document_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Document ID | 

### Return type

[**Document**](Document.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **document_show_all**
> list[Document] document_show_all()

List documents

Return a list of all documents

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
api_instance = api.DocumentApi()

try: 
    # List documents
    api_response = api_instance.document_show_all()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DocumentApi->document_show_all: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[Document]**](Document.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

