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


class NetworkConnection(object):
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
        'speed': 'int',
        'model': 'str',
        'manufacturer': 'str',
        'mac': 'str',
        'ipv4_address': 'str'
    }

    attribute_map = {
        'name': 'name',
        'speed': 'speed',
        'model': 'model',
        'manufacturer': 'manufacturer',
        'mac': 'mac',
        'ipv4_address': 'ipv4Address'
    }

    def __init__(self, name=None, speed=None, model=None, manufacturer=None, mac=None, ipv4_address=None):
        """
        NetworkConnection - a model defined in Swagger
        """

        self._name = None
        self._speed = None
        self._model = None
        self._manufacturer = None
        self._mac = None
        self._ipv4_address = None

        if name is not None:
          self.name = name
        if speed is not None:
          self.speed = speed
        if model is not None:
          self.model = model
        if manufacturer is not None:
          self.manufacturer = manufacturer
        if mac is not None:
          self.mac = mac
        if ipv4_address is not None:
          self.ipv4_address = ipv4_address

    @property
    def name(self):
        """
        Gets the name of this NetworkConnection.
        Network connection name

        :return: The name of this NetworkConnection.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """
        Sets the name of this NetworkConnection.
        Network connection name

        :param name: The name of this NetworkConnection.
        :type: str
        """

        self._name = name

    @property
    def speed(self):
        """
        Gets the speed of this NetworkConnection.
        Speed in bytes per second

        :return: The speed of this NetworkConnection.
        :rtype: int
        """
        return self._speed

    @speed.setter
    def speed(self, speed):
        """
        Sets the speed of this NetworkConnection.
        Speed in bytes per second

        :param speed: The speed of this NetworkConnection.
        :type: int
        """

        self._speed = speed

    @property
    def model(self):
        """
        Gets the model of this NetworkConnection.
        Device model name

        :return: The model of this NetworkConnection.
        :rtype: str
        """
        return self._model

    @model.setter
    def model(self, model):
        """
        Sets the model of this NetworkConnection.
        Device model name

        :param model: The model of this NetworkConnection.
        :type: str
        """

        self._model = model

    @property
    def manufacturer(self):
        """
        Gets the manufacturer of this NetworkConnection.
        Device manufacturer

        :return: The manufacturer of this NetworkConnection.
        :rtype: str
        """
        return self._manufacturer

    @manufacturer.setter
    def manufacturer(self, manufacturer):
        """
        Sets the manufacturer of this NetworkConnection.
        Device manufacturer

        :param manufacturer: The manufacturer of this NetworkConnection.
        :type: str
        """

        self._manufacturer = manufacturer

    @property
    def mac(self):
        """
        Gets the mac of this NetworkConnection.
        MAC address

        :return: The mac of this NetworkConnection.
        :rtype: str
        """
        return self._mac

    @mac.setter
    def mac(self, mac):
        """
        Sets the mac of this NetworkConnection.
        MAC address

        :param mac: The mac of this NetworkConnection.
        :type: str
        """

        self._mac = mac

    @property
    def ipv4_address(self):
        """
        Gets the ipv4_address of this NetworkConnection.
        IPv4 address

        :return: The ipv4_address of this NetworkConnection.
        :rtype: str
        """
        return self._ipv4_address

    @ipv4_address.setter
    def ipv4_address(self, ipv4_address):
        """
        Sets the ipv4_address of this NetworkConnection.
        IPv4 address

        :param ipv4_address: The ipv4_address of this NetworkConnection.
        :type: str
        """

        self._ipv4_address = ipv4_address

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
        if not isinstance(other, NetworkConnection):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
