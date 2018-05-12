# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.vendor import Vendor
from api.models.vendor_parameters import VendorParameters
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestVendorController(BaseTestCase):
    """ VendorController integration test stubs """

    def test_vendor_create(self):
        """
        Test case for vendor_create

        Create a new vendor
        """
        body = VendorParameters()
        response = self.client.open('/api/v1/vendor',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_vendor_delete(self):
        """
        Test case for vendor_delete

        Delete a vendor
        """
        response = self.client.open('/api/v1/vendor/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_vendor_show(self):
        """
        Test case for vendor_show

        Show a single vendor
        """
        response = self.client.open('/api/v1/vendor/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_vendor_showall(self):
        """
        Test case for vendor_showall

        List vendors
        """
        response = self.client.open('/api/v1/vendor',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_vendor_update(self):
        """
        Test case for vendor_update

        Update a vendor
        """
        body = VendorParameters()
        response = self.client.open('/api/v1/vendor/{id}'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
