# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.invoice import Invoice
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestInvoiceController(BaseTestCase):
    """ InvoiceController integration test stubs """

    def test_invoice_create(self):
        """
        Test case for invoice_create

        Create a new invoice
        """
        data = dict(name='name_example',
                    description='description_example',
                    file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open('/api/v1/invoice',
                                    method='POST',
                                    data=data,
                                    content_type='multipart/form-data')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_delete(self):
        """
        Test case for invoice_delete

        Delete an invoice
        """
        response = self.client.open('/api/v1/invoice/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_process(self):
        """
        Test case for invoice_process

        Start invoice processing
        """
        response = self.client.open('/api/v1/invoice/{id}/process'.format(id='id_example'),
                                    method='POST')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_show(self):
        """
        Test case for invoice_show

        Show a single invoice
        """
        response = self.client.open('/api/v1/invoice/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_show_all(self):
        """
        Test case for invoice_show_all

        List invoices
        """
        query_string = [('summary', true)]
        response = self.client.open('/api/v1/invoice',
                                    method='GET',
                                    query_string=query_string)
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_invoice_update(self):
        """
        Test case for invoice_update

        Update an invoice
        """
        body = Invoice()
        response = self.client.open('/api/v1/invoice/{id}'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
