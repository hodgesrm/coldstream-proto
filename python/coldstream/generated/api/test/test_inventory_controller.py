# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.host import Host
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestInventoryController(BaseTestCase):
    """ InventoryController integration test stubs """

    def test_inventory_host_create(self):
        """
        Test case for inventory_host_create

        Create a new host inventory entry
        """
        body = Host()
        response = self.client.open('/api/v1/host',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_inventory_host_delete(self):
        """
        Test case for inventory_host_delete

        Delete a host inventory record
        """
        response = self.client.open('/api/v1/host/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_inventory_host_show_all(self):
        """
        Test case for inventory_host_show_all

        List host entries
        """
        query_string = [('summary', true)]
        response = self.client.open('/api/v1/host',
                                    method='GET',
                                    query_string=query_string)
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_host_show(self):
        """
        Test case for invoice_host_show

        Show a single host inventory record
        """
        response = self.client.open('/api/v1/host/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
