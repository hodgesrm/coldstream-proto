# api.DataApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**data_create**](DataApi.md#data_create) | **POST** /data | Upload data series
[**data_delete**](DataApi.md#data_delete) | **DELETE** /data/{id} | Delete a data series
[**data_process**](DataApi.md#data_process) | **POST** /data/{id}/process | Kick off background processing of data series
[**data_show**](DataApi.md#data_show) | **GET** /data/{id} | Return data series metadata
[**data_show_all**](DataApi.md#data_show_all) | **GET** /data | List data serties
[**data_show_content**](DataApi.md#data_show_content) | **GET** /data/{id}/content | Return data series content
[**data_update**](DataApi.md#data_update) | **PUT** /data/{id} | Update a data series


# **data_create**
> DataSeries data_create(file, description=description, tags=tags, process=process)

Upload data series

Upload data series in a file for analysis

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
api_instance = api.DataApi()
file = '/path/to/file.txt' # file | Data series content
description = 'description_example' # str | An optional description of the data series (optional)
tags = 'tags_example' # str | Optional tags that apply to this entity passed as a JSON string containing name-value pairs. (optional)
process = true # bool | Optional flag to kick off processing automatically if true (optional) (default to true)

try: 
    # Upload data series
    api_response = api_instance.data_create(file, description=description, tags=tags, process=process)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DataApi->data_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **file**| Data series content | 
 **description** | **str**| An optional description of the data series | [optional] 
 **tags** | **str**| Optional tags that apply to this entity passed as a JSON string containing name-value pairs. | [optional] 
 **process** | **bool**| Optional flag to kick off processing automatically if true | [optional] [default to true]

### Return type

[**DataSeries**](DataSeries.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_delete**
> data_delete(id)

Delete a data series

Delete a data series and any derived information

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
api_instance = api.DataApi()
id = 'id_example' # str | Series ID

try: 
    # Delete a data series
    api_instance.data_delete(id)
except ApiException as e:
    print("Exception when calling DataApi->data_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_process**
> data_process(id)

Kick off background processing of data series

Run background processing of data series, which may generate one or more inventory records.

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
api_instance = api.DataApi()
id = 'id_example' # str | Series ID

try: 
    # Kick off background processing of data series
    api_instance.data_process(id)
except ApiException as e:
    print("Exception when calling DataApi->data_process: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_show**
> DataSeries data_show(id)

Return data series metadata

Download data series metadata without content

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
api_instance = api.DataApi()
id = 'id_example' # str | Series ID

try: 
    # Return data series metadata
    api_response = api_instance.data_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DataApi->data_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

[**DataSeries**](DataSeries.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_show_all**
> list[DataSeries] data_show_all()

List data serties

Return a list of metadata entries for all data series

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
api_instance = api.DataApi()

try: 
    # List data serties
    api_response = api_instance.data_show_all()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling DataApi->data_show_all: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[DataSeries]**](DataSeries.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_show_content**
> data_show_content(id)

Return data series content

Download data series content

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
api_instance = api.DataApi()
id = 'id_example' # str | Series ID

try: 
    # Return data series content
    api_instance.data_show_content(id)
except ApiException as e:
    print("Exception when calling DataApi->data_show_content: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/octet-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **data_update**
> data_update(id, body=body)

Update a data series

Update data series description and/or tags.  Other fields are ignored if included in the body.

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
api_instance = api.DataApi()
id = 'id_example' # str | Data series ID
body = api.DataSeries() # DataSeries | Data series parameters (optional)

try: 
    # Update a data series
    api_instance.data_update(id, body=body)
except ApiException as e:
    print("Exception when calling DataApi->data_update: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Data series ID | 
 **body** | [**DataSeries**](DataSeries.md)| Data series parameters | [optional] 

### Return type

void (empty response body)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

