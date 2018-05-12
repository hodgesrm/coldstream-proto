# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.document import Document
from api.models.document_parameters import DocumentParameters
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestDocumentController(BaseTestCase):
    """ DocumentController integration test stubs """

    def test_document_create(self):
        """
        Test case for document_create

        Upload document
        """
        data = dict(description='description_example',
                    scan=true,
                    file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open('/api/v1/document',
                                    method='POST',
                                    data=data,
                                    content_type='multipart/form-data')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_delete(self):
        """
        Test case for document_delete

        Delete a invoice
        """
        response = self.client.open('/api/v1/document/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_fetch_content(self):
        """
        Test case for document_fetch_content

        Return document content
        """
        response = self.client.open('/api/v1/document/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_scan(self):
        """
        Test case for document_scan

        Kick off document scanning
        """
        response = self.client.open('/api/v1/document/{id}/scan'.format(id='id_example'),
                                    method='POST')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_show(self):
        """
        Test case for document_show

        Return document metadata
        """
        response = self.client.open('/api/v1/document/{id}'.format(id='id_example'),
                                    method='HEAD')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_show_all(self):
        """
        Test case for document_show_all

        List documents
        """
        response = self.client.open('/api/v1/document',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_document_update(self):
        """
        Test case for document_update

        Update document information
        """
        body = DocumentParameters()
        response = self.client.open('/api/v1/document/{id}'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
