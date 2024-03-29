# coding: utf-8

"""
    Goldfin Service API

    REST API for Goldfin Intelligent Invoice Processing

    OpenAPI spec version: 1.0.0
    Contact: info@goldfin.io
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from __future__ import absolute_import

import os
import sys
import unittest

import api
from api.rest import ApiException
from api.apis.security_api import SecurityApi


class TestSecurityApi(unittest.TestCase):
    """ SecurityApi unit test stubs """

    def setUp(self):
        self.api = api.apis.security_api.SecurityApi()

    def tearDown(self):
        pass

    def test_login_by_credentials(self):
        """
        Test case for login_by_credentials

        Login to system
        """
        pass

    def test_logout(self):
        """
        Test case for logout

        Logout from system
        """
        pass


if __name__ == '__main__':
    unittest.main()
