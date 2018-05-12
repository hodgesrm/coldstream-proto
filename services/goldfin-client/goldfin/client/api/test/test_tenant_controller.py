# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.tenant import Tenant
from api.models.tenant_parameters import TenantParameters
from api.models.user import User
from api.models.user_parameters import UserParameters
from api.models.user_password_parameters import UserPasswordParameters
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestTenantController(BaseTestCase):
    """ TenantController integration test stubs """

    def test_tenant_create(self):
        """
        Test case for tenant_create

        Create a new tenant
        """
        body = TenantParameters()
        response = self.client.open('/api/v1/tenant',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_delete(self):
        """
        Test case for tenant_delete

        Delete a tenant
        """
        response = self.client.open('/api/v1/tenant/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_show(self):
        """
        Test case for tenant_show

        Show a single tenant
        """
        response = self.client.open('/api/v1/tenant/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_showall(self):
        """
        Test case for tenant_showall

        List tenants
        """
        response = self.client.open('/api/v1/tenant',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_tenant_update(self):
        """
        Test case for tenant_update

        Update a tenant
        """
        body = TenantParameters()
        response = self.client.open('/api/v1/tenant/{id}'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_create(self):
        """
        Test case for user_create

        Create a new user for a tenant
        """
        body = UserParameters()
        response = self.client.open('/api/v1/user',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_delete(self):
        """
        Test case for user_delete

        Delete a user
        """
        response = self.client.open('/api/v1/user/{id}'.format(id='id_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_show(self):
        """
        Test case for user_show

        Show a single user
        """
        response = self.client.open('/api/v1/user/{id}'.format(id='id_example'),
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_showall(self):
        """
        Test case for user_showall

        List users
        """
        response = self.client.open('/api/v1/user',
                                    method='GET')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_update(self):
        """
        Test case for user_update

        Update a user
        """
        body = UserParameters()
        response = self.client.open('/api/v1/user/{id}'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_user_update_password(self):
        """
        Test case for user_update_password

        Update user password
        """
        body = UserPasswordParameters()
        response = self.client.open('/api/v1/user/{id}/password'.format(id='id_example'),
                                    method='PUT',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
