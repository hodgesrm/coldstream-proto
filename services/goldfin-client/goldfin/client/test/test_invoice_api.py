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
from api.apis.invoice_api import InvoiceApi


class TestInvoiceApi(unittest.TestCase):
    """ InvoiceApi unit test stubs """

    def setUp(self):
        self.api = api.apis.invoice_api.InvoiceApi()

    def tearDown(self):
        pass

    def test_invoice_delete(self):
        """
        Test case for invoice_delete

        Delete an invoice
        """
        pass

    def test_invoice_download(self):
        """
        Test case for invoice_download

        Download invoice document
        """
        pass

    def test_invoice_show(self):
        """
        Test case for invoice_show

        Show a single invoice
        """
        pass

    def test_invoice_show_all(self):
        """
        Test case for invoice_show_all

        List invoices
        """
        pass

    def test_invoice_validate(self):
        """
        Test case for invoice_validate

        Start invoice validations
        """
        pass


if __name__ == '__main__':
    unittest.main()
