# api.ExtractApi

All URIs are relative to *https://api.goldfin.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**extract_download**](ExtractApi.md#extract_download) | **GET** /extract/download | Download a data extract
[**extract_types**](ExtractApi.md#extract_types) | **GET** /extract/types | List available extract types


# **extract_download**
> file extract_download(extract_type, filter=filter, output=output)

Download a data extract

Extract data for a particular extract type, where these correspond to schema types as well as  reports that join data from multiple schema types

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
api_instance = api.ExtractApi()
extract_type = 'extract_type_example' # str | Name of the extract type
filter = 'filter_example' # str | A query string that specifies extract content.  If omitted all entities are selected. (optional)
output = 'output_example' # str | Selects the extract output type. Currently only CSV is supported. (optional)

try: 
    # Download a data extract
    api_response = api_instance.extract_download(extract_type, filter=filter, output=output)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ExtractApi->extract_download: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **extract_type** | **str**| Name of the extract type | 
 **filter** | **str**| A query string that specifies extract content.  If omitted all entities are selected. | [optional] 
 **output** | **str**| Selects the extract output type. Currently only CSV is supported. | [optional] 

### Return type

[**file**](file.md)

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/csv, text/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **extract_types**
> list[str] extract_types()

List available extract types

Return a list of available extrac types

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
api_instance = api.ExtractApi()

try: 
    # List available extract types
    api_response = api_instance.extract_types()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ExtractApi->extract_types: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

**list[str]**

### Authorization

[ApiKey](../README.md#ApiKey), [SessionKey](../README.md#SessionKey)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

