# api.ServerApi

All URIs are relative to *http://api.coldstream.io/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**server_version**](ServerApi.md#server_version) | **GET** /server/version | Return server version.


# **server_version**
> VersionInfo server_version()

Return server version.

Return server version information.

### Example 
```python
from __future__ import print_function
import time
import api
from api.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = api.ServerApi()

try: 
    # Return server version.
    api_response = api_instance.server_version()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ServerApi->server_version: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**VersionInfo**](VersionInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

