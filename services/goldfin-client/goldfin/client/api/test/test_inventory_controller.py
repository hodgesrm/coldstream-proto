# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.host import Host
from api.models.observation import Observation
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestInventoryController(BaseTestCase):
    """ InventoryController integration test stubs """

    def test_host_delete(self):
        """
        Test case for host_delete

        Delete host record
        """
        response = self.client.open('/api/v1/host/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_host_show(self):
        """
        Test case for host_show

        Show single host inventory record
        """
        response = self.client.open('/api/v1/host/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_host_show_all(self):
        """
        Test case for host_show_all

        List current host inventory records
        """
        response = self.client.open('/api/v1/host',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_create(self):
        """
        Test case for observation_create

        Upload observation
        """
        data = dict(file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open('/api/v1/observation',
                                    method='POST',
                                    data=data,
                                    content_type='multipart/form-data')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_delete(self):
        """
        Test case for observation_delete

        Delete a invoice
        """
        response = self.client.open('/api/v1/observation/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_fetch_content(self):
        """
        Test case for observation_fetch_content

        Return observation content
        """
        response = self.client.open('/api/v1/observation/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_process(self):
        """
        Test case for observation_process

        Kick off background processing of observation
        """
        response = self.client.open('/api/v1/observation/{id}/process'.format(id='id_example'),
                                    method='POST')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_show(self):
        """
        Test case for observation_show

        Return observation metadata
        """
        response = self.client.open('/api/v1/observation/{id}'.format(id='id_example'),
                                    method='HEAD')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_observation_show_all(self):
        """
        Test case for observation_show_all

        List observations
        """
        response = self.client.open('/api/v1/observation',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
