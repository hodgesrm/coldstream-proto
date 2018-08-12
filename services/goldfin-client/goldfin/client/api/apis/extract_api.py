# coding: utf-8

"""
    Goldfin Service API

    REST API for Goldfin Intelligent Invoice Processing

    OpenAPI spec version: 1.0.0
    Contact: info@goldfin.io
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from __future__ import absolute_import

import sys
import os
import re

# python 2 and python 3 compatibility library
from six import iteritems

from ..configuration import Configuration
from ..api_client import ApiClient


class ExtractApi(object):
    """
    NOTE: This class is auto generated by the swagger code generator program.
    Do not edit the class manually.
    Ref: https://github.com/swagger-api/swagger-codegen
    """

    def __init__(self, api_client=None):
        config = Configuration()
        if api_client:
            self.api_client = api_client
        else:
            if not config.api_client:
                config.api_client = ApiClient()
            self.api_client = config.api_client

    def extract_download(self, extract_type, **kwargs):
        """
        Download a data extract
        Extract data for a particular extract type, where these correspond to schema types as well as  reports that join data from multiple schema types
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please define a `callback` function
        to be invoked when receiving the response.
        >>> def callback_function(response):
        >>>     pprint(response)
        >>>
        >>> thread = api.extract_download(extract_type, callback=callback_function)

        :param callback function: The callback function
            for asynchronous request. (optional)
        :param str extract_type: Name of the extract type (required)
        :param str filter: A query string that specifies extract content.  If omitted all entities are selected.
        :param str output: Selects the extract output type. Currently only CSV is supported.
        :return: file
                 If the method is called asynchronously,
                 returns the request thread.
        """
        kwargs['_return_http_data_only'] = True
        if kwargs.get('callback'):
            return self.extract_download_with_http_info(extract_type, **kwargs)
        else:
            (data) = self.extract_download_with_http_info(extract_type, **kwargs)
            return data

    def extract_download_with_http_info(self, extract_type, **kwargs):
        """
        Download a data extract
        Extract data for a particular extract type, where these correspond to schema types as well as  reports that join data from multiple schema types
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please define a `callback` function
        to be invoked when receiving the response.
        >>> def callback_function(response):
        >>>     pprint(response)
        >>>
        >>> thread = api.extract_download_with_http_info(extract_type, callback=callback_function)

        :param callback function: The callback function
            for asynchronous request. (optional)
        :param str extract_type: Name of the extract type (required)
        :param str filter: A query string that specifies extract content.  If omitted all entities are selected.
        :param str output: Selects the extract output type. Currently only CSV is supported.
        :return: file
                 If the method is called asynchronously,
                 returns the request thread.
        """

        all_params = ['extract_type', 'filter', 'output']
        all_params.append('callback')
        all_params.append('_return_http_data_only')
        all_params.append('_preload_content')
        all_params.append('_request_timeout')

        params = locals()
        for key, val in iteritems(params['kwargs']):
            if key not in all_params:
                raise TypeError(
                    "Got an unexpected keyword argument '%s'"
                    " to method extract_download" % key
                )
            params[key] = val
        del params['kwargs']
        # verify the required parameter 'extract_type' is set
        if ('extract_type' not in params) or (params['extract_type'] is None):
            raise ValueError("Missing the required parameter `extract_type` when calling `extract_download`")


        collection_formats = {}

        path_params = {}

        query_params = []
        if 'extract_type' in params:
            query_params.append(('extractType', params['extract_type']))
        if 'filter' in params:
            query_params.append(('filter', params['filter']))
        if 'output' in params:
            query_params.append(('output', params['output']))

        header_params = {}

        form_params = []
        local_var_files = {}

        body_params = None
        # HTTP header `Accept`
        header_params['Accept'] = self.api_client.\
            select_header_accept(['text/csv', 'text/json'])

        # Authentication setting
        auth_settings = ['APIKeyHeader']

        return self.api_client.call_api('/extract/download', 'GET',
                                        path_params,
                                        query_params,
                                        header_params,
                                        body=body_params,
                                        post_params=form_params,
                                        files=local_var_files,
                                        response_type='file',
                                        auth_settings=auth_settings,
                                        callback=params.get('callback'),
                                        _return_http_data_only=params.get('_return_http_data_only'),
                                        _preload_content=params.get('_preload_content', True),
                                        _request_timeout=params.get('_request_timeout'),
                                        collection_formats=collection_formats)

    def extract_types(self, **kwargs):
        """
        List available extract types
        Return a list of available extrac types
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please define a `callback` function
        to be invoked when receiving the response.
        >>> def callback_function(response):
        >>>     pprint(response)
        >>>
        >>> thread = api.extract_types(callback=callback_function)

        :param callback function: The callback function
            for asynchronous request. (optional)
        :return: list[str]
                 If the method is called asynchronously,
                 returns the request thread.
        """
        kwargs['_return_http_data_only'] = True
        if kwargs.get('callback'):
            return self.extract_types_with_http_info(**kwargs)
        else:
            (data) = self.extract_types_with_http_info(**kwargs)
            return data

    def extract_types_with_http_info(self, **kwargs):
        """
        List available extract types
        Return a list of available extrac types
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please define a `callback` function
        to be invoked when receiving the response.
        >>> def callback_function(response):
        >>>     pprint(response)
        >>>
        >>> thread = api.extract_types_with_http_info(callback=callback_function)

        :param callback function: The callback function
            for asynchronous request. (optional)
        :return: list[str]
                 If the method is called asynchronously,
                 returns the request thread.
        """

        all_params = []
        all_params.append('callback')
        all_params.append('_return_http_data_only')
        all_params.append('_preload_content')
        all_params.append('_request_timeout')

        params = locals()
        for key, val in iteritems(params['kwargs']):
            if key not in all_params:
                raise TypeError(
                    "Got an unexpected keyword argument '%s'"
                    " to method extract_types" % key
                )
            params[key] = val
        del params['kwargs']

        collection_formats = {}

        path_params = {}

        query_params = []

        header_params = {}

        form_params = []
        local_var_files = {}

        body_params = None
        # HTTP header `Accept`
        header_params['Accept'] = self.api_client.\
            select_header_accept(['application/json'])

        # Authentication setting
        auth_settings = ['APIKeyHeader']

        return self.api_client.call_api('/extract/types', 'GET',
                                        path_params,
                                        query_params,
                                        header_params,
                                        body=body_params,
                                        post_params=form_params,
                                        files=local_var_files,
                                        response_type='list[str]',
                                        auth_settings=auth_settings,
                                        callback=params.get('callback'),
                                        _return_http_data_only=params.get('_return_http_data_only'),
                                        _preload_content=params.get('_preload_content', True),
                                        _request_timeout=params.get('_request_timeout'),
                                        collection_formats=collection_formats)