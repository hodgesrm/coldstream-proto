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


class OperatingSystem(object):
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
        'name': 'str',
        'distribution': 'str',
        'major_version': 'str',
        'minor_version': 'str'
    }

    attribute_map = {
        'name': 'name',
        'distribution': 'distribution',
        'major_version': 'majorVersion',
        'minor_version': 'minorVersion'
    }

    def __init__(self, name=None, distribution=None, major_version=None, minor_version=None):
        """
        OperatingSystem - a model defined in Swagger
        """

        self._name = None
        self._distribution = None
        self._major_version = None
        self._minor_version = None

        if name is not None:
          self.name = name
        if distribution is not None:
          self.distribution = distribution
        if major_version is not None:
          self.major_version = major_version
        if minor_version is not None:
          self.minor_version = minor_version

    @property
    def name(self):
        """
        Gets the name of this OperatingSystem.
        OS major version e.g., Linux

        :return: The name of this OperatingSystem.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """
        Sets the name of this OperatingSystem.
        OS major version e.g., Linux

        :param name: The name of this OperatingSystem.
        :type: str
        """

        self._name = name

    @property
    def distribution(self):
        """
        Gets the distribution of this OperatingSystem.
        Distribution, e.g, Debian or Ubuntu

        :return: The distribution of this OperatingSystem.
        :rtype: str
        """
        return self._distribution

    @distribution.setter
    def distribution(self, distribution):
        """
        Sets the distribution of this OperatingSystem.
        Distribution, e.g, Debian or Ubuntu

        :param distribution: The distribution of this OperatingSystem.
        :type: str
        """

        self._distribution = distribution

    @property
    def major_version(self):
        """
        Gets the major_version of this OperatingSystem.
        OS major version e.g., 'Waxing Wombat' or 10

        :return: The major_version of this OperatingSystem.
        :rtype: str
        """
        return self._major_version

    @major_version.setter
    def major_version(self, major_version):
        """
        Sets the major_version of this OperatingSystem.
        OS major version e.g., 'Waxing Wombat' or 10

        :param major_version: The major_version of this OperatingSystem.
        :type: str
        """

        self._major_version = major_version

    @property
    def minor_version(self):
        """
        Gets the minor_version of this OperatingSystem.
        OS minor version e.g., '0'

        :return: The minor_version of this OperatingSystem.
        :rtype: str
        """
        return self._minor_version

    @minor_version.setter
    def minor_version(self, minor_version):
        """
        Sets the minor_version of this OperatingSystem.
        OS minor version e.g., '0'

        :param minor_version: The minor_version of this OperatingSystem.
        :type: str
        """

        self._minor_version = minor_version

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
        if not isinstance(other, OperatingSystem):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
