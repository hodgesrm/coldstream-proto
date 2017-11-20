# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestExportController(BaseTestCase):
    """ ExportController integration test stubs """

    def test_export_invoice(self):
        """
        Test case for export_invoice

        Export invoice data
        """
        query_string = [('outputFormat', 'outputFormat_example'),
                        ('timeSlice', 'timeSlice_example'),
                        ('filterSpec', 'filterSpec_example')]
        response = self.client.open('/api/v1/export/invoice',
                                    method='GET',
                                    query_string=query_string)
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
