# coding: utf-8

"""
    Goldfin Storage API

    REST API for Goldfin Storage Microservice

    OpenAPI spec version: 1.0.0
    Contact: info@goldfin.io
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from pprint import pformat
from six import iteritems
import re


class ApiResponse(object):
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
        'code': 'int',
        'type': 'str',
        'message': 'str'
    }

    attribute_map = {
        'code': 'code',
        'type': 'type',
        'message': 'message'
    }

    def __init__(self, code=None, type=None, message=None):
        """
        ApiResponse - a model defined in Swagger
        """

        self._code = None
        self._type = None
        self._message = None

        if code is not None:
          self.code = code
        if type is not None:
          self.type = type
        if message is not None:
          self.message = message

    @property
    def code(self):
        """
        Gets the code of this ApiResponse.

        :return: The code of this ApiResponse.
        :rtype: int
        """
        return self._code

    @code.setter
    def code(self, code):
        """
        Sets the code of this ApiResponse.

        :param code: The code of this ApiResponse.
        :type: int
        """

        self._code = code

    @property
    def type(self):
        """
        Gets the type of this ApiResponse.

        :return: The type of this ApiResponse.
        :rtype: str
        """
        return self._type

    @type.setter
    def type(self, type):
        """
        Sets the type of this ApiResponse.

        :param type: The type of this ApiResponse.
        :type: str
        """

        self._type = type

    @property
    def message(self):
        """
        Gets the message of this ApiResponse.

        :return: The message of this ApiResponse.
        :rtype: str
        """
        return self._message

    @message.setter
    def message(self, message):
        """
        Sets the message of this ApiResponse.

        :param message: The message of this ApiResponse.
        :type: str
        """

        self._message = message

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
        if not isinstance(other, ApiResponse):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
