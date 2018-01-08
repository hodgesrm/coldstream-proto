# coding: utf-8

from __future__ import absolute_import

from goldfin_storage.models.api_response import ApiResponse
from goldfin_storage.models.document import Document
from goldfin_storage.models.tenant_info import TenantInfo
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestStorageController(BaseTestCase):
    """ StorageController integration test stubs """

    def test_tenant_create(self):
        """
        Test case for tenant_create

        Create storage folder for tenant
        """
        tenant = TenantInfo()
        response = self.client.open('/api/v1/tenant-data',
                                    method='POST',
                                    data=json.dumps(tenant),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_delete(self):
        """
        Test case for tenant_delete

        Delete a tenant
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}'.format(tenantId='tenantId_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_document_content(self):
        """
        Test case for tenant_document_content

        Download document content
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}/document/{id}'.format(tenantId='tenantId_example', id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_document_create(self):
        """
        Test case for tenant_document_create

        Create a new document for tenant
        """
        data = dict(name='name_example',
                    description='description_example',
                    file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open('/api/v1/tenant-data/{tenantId}/document'.format(tenantId='tenantId_example'),
                                    method='POST',
                                    data=data,
                                    content_type='multipart/form-data')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_document_delete(self):
        """
        Test case for tenant_document_delete

        Delete a document
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}/document/{id}'.format(tenantId='tenantId_example', id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_document_metadata(self):
        """
        Test case for tenant_document_metadata

        Show document metadata
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}/document/{id}'.format(tenantId='tenantId_example', id='id_example'),
                                    method='HEAD')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_document_showall(self):
        """
        Test case for tenant_document_showall

        List tenant documents
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}/document'.format(tenantId='tenantId_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_show(self):
        """
        Test case for tenant_show

        Show tenant metadata
        """
        response = self.client.open('/api/v1/tenant-data/{tenantId}'.format(tenantId='tenantId_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_show_all(self):
        """
        Test case for tenant_show_all

        List tenant storage folder
        """
        response = self.client.open('/api/v1/tenant-data',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
