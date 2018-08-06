# api.InventoryApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**data_create**](InventoryApi.md#data_create) | **POST** /data | Upload data series
[**data_delete**](InventoryApi.md#data_delete) | **DELETE** /data/{id} | Delete a data series
[**data_process**](InventoryApi.md#data_process) | **POST** /data/{id}/process | Kick off background processing of data series
[**data_show**](InventoryApi.md#data_show) | **GET** /data/{id} | Return data series metadata
[**data_show_all**](InventoryApi.md#data_show_all) | **GET** /data | List data serties
[**data_show_content**](InventoryApi.md#data_show_content) | **GET** /data/{id}/content | Return data series content
[**host_delete**](InventoryApi.md#host_delete) | **DELETE** /host/{id} | Delete host record
[**host_show**](InventoryApi.md#host_show) | **GET** /host/{id} | Show single host inventory record
[**host_show_all**](InventoryApi.md#host_show_all) | **GET** /host | List current host inventory records


# **data_create**
> DataSeries data_create(file, description=description, process=process)

Upload data series

Upload data series in a file for analysis

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
api_instance = api.InventoryApi()
file = '/path/to/file.txt' # file | Data series content
description = 'description_example' # str | A optional description of the data series (optional)
process = true # bool | Optional flag to kick off processing automatically if true (optional) (default to true)

try: 
    # Upload data series
    api_response = api_instance.data_create(file, description=description, process=process)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InventoryApi->data_create: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **file**| Data series content | 
 **description** | **str**| A optional description of the data series | [optional] 
 **process** | **bool**| Optional flag to kick off processing automatically if true | [optional] [default to true]

### Return type

[**DataSeries**](DataSeries.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.InventoryApi()
id = 'id_example' # str | Series ID

try: 
    # Delete a data series
    api_instance.data_delete(id)
except ApiException as e:
    print("Exception when calling InventoryApi->data_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.InventoryApi()
id = 'id_example' # str | Series ID

try: 
    # Kick off background processing of data series
    api_instance.data_process(id)
except ApiException as e:
    print("Exception when calling InventoryApi->data_process: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.InventoryApi()
id = 'id_example' # str | Series ID

try: 
    # Return data series metadata
    api_response = api_instance.data_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InventoryApi->data_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

[**DataSeries**](DataSeries.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.InventoryApi()

try: 
    # List data serties
    api_response = api_instance.data_show_all()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InventoryApi->data_show_all: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[DataSeries]**](DataSeries.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

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

# Configure API key authorization: APIKeyHeader
api.configuration.api_key['vnd.io.goldfin.session'] = 'YOUR_API_KEY'
# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# api.configuration.api_key_prefix['vnd.io.goldfin.session'] = 'Bearer'

# create an instance of the API class
api_instance = api.InventoryApi()
id = 'id_example' # str | Series ID

try: 
    # Return data series content
    api_instance.data_show_content(id)
except ApiException as e:
    print("Exception when calling InventoryApi->data_show_content: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Series ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/octet-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **host_delete**
> host_delete(id)

Delete host record

Delete a host record.  It can be recreated by rescanning the corresponding document

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
api_instance = api.InventoryApi()
id = 'id_example' # str | Invoice ID

try: 
    # Delete host record
    api_instance.host_delete(id)
except ApiException as e:
    print("Exception when calling InventoryApi->host_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Invoice ID | 

### Return type

void (empty response body)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **host_show**
> Host host_show(id)

Show single host inventory record

Returns the most recent inventory record for a specific host.  The host must be identified by the resource ID or internal ID

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
api_instance = api.InventoryApi()
id = 'id_example' # str | Host resource ID

try: 
    # Show single host inventory record
    api_response = api_instance.host_show(id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InventoryApi->host_show: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Host resource ID | 

### Return type

[**Host**](Host.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **host_show_all**
> list[Host] host_show_all()

List current host inventory records

Return a list of current hosts in inventory.  This returns the most recent record for each host.

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
api_instance = api.InventoryApi()

try: 
    # List current host inventory records
    api_response = api_instance.host_show_all()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling InventoryApi->host_show_all: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**list[Host]**](Host.md)

### Authorization

[APIKeyHeader](../README.md#APIKeyHeader)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

