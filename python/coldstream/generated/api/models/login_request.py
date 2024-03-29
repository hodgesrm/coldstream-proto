# coding: utf-8

"""
    Goldfin Invoice Processing API

    Goldfin Invoice Analysis

    OpenAPI spec version: 1.0.0
    Contact: rhodges@skylineresearch.comm
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from pprint import pformat
from six import iteritems
import re


class LoginRequest(object):
    """
    NOTE: This class is auto generated by the swagger code generator program.
    Do not edit the class manually.
    """


    """
    Attributes:
      swagger_types (dict): The key is attribute name
                            and the value is attribute type.
      attribute_map (dict): The key is attribute name
                            and the value is json key in definition.
    """
    swagger_types = {
        'user': 'str',
        'password': 'str'
    }

    attribute_map = {
        'user': 'user',
        'password': 'password'
    }

    def __init__(self, user=None, password=None):
        """
        LoginRequest - a model defined in Swagger
        """

        self._user = None
        self._password = None

        if user is not None:
          self.user = user
        if password is not None:
          self.password = password

    @property
    def user(self):
        """
        Gets the user of this LoginRequest.
        User name

        :return: The user of this LoginRequest.
        :rtype: str
        """
        return self._user

    @user.setter
    def user(self, user):
        """
        Sets the user of this LoginRequest.
        User name

        :param user: The user of this LoginRequest.
        :type: str
        """

        self._user = user

    @property
    def password(self):
        """
        Gets the password of this LoginRequest.
        User password

        :return: The password of this LoginRequest.
        :rtype: str
        """
        return self._password

    @password.setter
    def password(self, password):
        """
        Sets the password of this LoginRequest.
        User password

        :param password: The password of this LoginRequest.
        :type: str
        """

        self._password = password

    def to_dict(self):
        """
        Returns the model properties as a dict
        """
        result = {}

        for attr, _ in iteritems(self.swagger_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(map(
                    lambda x: x.to_dict() if hasattr(x, "to_dict") else x,
                    value
                ))
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(map(
                    lambda item: (item[0], item[1].to_dict())
                    if hasattr(item[1], "to_dict") else item,
                    value.items()
                ))
            else:
                result[attr] = value

        return result

    def to_str(self):
        """
        Returns the string representation of the model
        """
        return pformat(self.to_dict())

    def __repr__(self):
        """
        For `print` and `pprint`
        """
        return self.to_str()

    def __eq__(self, other):
        """
        Returns true if both objects are equal
        """
        if not isinstance(other, LoginRequest):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
