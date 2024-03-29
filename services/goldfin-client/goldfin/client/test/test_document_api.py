# coding: utf-8

"""
    Goldfin Service API

    REST API for Goldfin Intelligent Invoice Processing

    OpenAPI spec version: 1.0.0
    Contact: info@goldfin.io
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from __future__ import absolute_import

import os
import sys
import unittest

import api
from api.rest import ApiException
from api.apis.document_api import DocumentApi


class TestDocumentApi(unittest.TestCase):
    """ DocumentApi unit test stubs """

    def setUp(self):
        self.api = api.apis.document_api.DocumentApi()

    def tearDown(self):
        pass

    def test_document_create(self):
        """
        Test case for document_create

        Upload document
        """
        pass

    def test_document_delete(self):
        """
        Test case for document_delete

        Delete a invoice
        """
        pass

    def test_document_download(self):
        """
        Test case for document_download

        Download content
        """
        pass

    def test_document_process(self):
        """
        Test case for document_process

        Kick off document analysis
        """
        pass

    def test_document_show(self):
        """
        Test case for document_show

        Return document metadata
        """
        pass

    def test_document_show_all(self):
        """
        Test case for document_show_all

        List documents
        """
        pass


if __name__ == '__main__':
    unittest.main()
