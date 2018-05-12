# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.login_credentials import LoginCredentials
from . import BaseTestCase
from six import BytesIO
from flask import json


class TestSecurityController(BaseTestCase):
    """ SecurityController integration test stubs """

    def test_login_by_credentials(self):
        """
        Test case for login_by_credentials

        Login to system
        """
        body = LoginCredentials()
        response = self.client.open('/api/v1/session',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))

    def test_logout(self):
        """
        Test case for logout

        Logout from system
        """
        response = self.client.open('/api/v1/session/{token}'.format(token='token_example'),
                                    method='DELETE')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
