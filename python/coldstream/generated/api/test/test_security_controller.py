# coding: utf-8

from __future__ import absolute_import

from api.models.api_response import ApiResponse
from api.models.login_request import LoginRequest
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
        body = LoginRequest()
        response = self.client.open('/api/v1/login',
                                    method='POST',
                                    data=json.dumps(body),
                                    content_type='application/json')
        self.assert200(response, "Response body is : " + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
