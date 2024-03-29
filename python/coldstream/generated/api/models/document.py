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


class Document(object):
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
        'id': 'str',
        'name': 'str',
        'locator': 'str',
        'thumbprint': 'str',
        'creation_date': 'str'
    }

    attribute_map = {
        'id': 'id',
        'name': 'name',
        'locator': 'locator',
        'thumbprint': 'thumbprint',
        'creation_date': 'creationDate'
    }

    def __init__(self, id=None, name=None, locator=None, thumbprint=None, creation_date=None):
        """
        Document - a model defined in Swagger
        """

        self._id = None
        self._name = None
        self._locator = None
        self._thumbprint = None
        self._creation_date = None

        if id is not None:
          self.id = id
        if name is not None:
          self.name = name
        if locator is not None:
          self.locator = locator
        if thumbprint is not None:
          self.thumbprint = thumbprint
        if creation_date is not None:
          self.creation_date = creation_date

    @property
    def id(self):
        """
        Gets the id of this Document.
        Document ID

        :return: The id of this Document.
        :rtype: str
        """
        return self._id

    @id.setter
    def id(self, id):
        """
        Sets the id of this Document.
        Document ID

        :param id: The id of this Document.
        :type: str
        """

        self._id = id

    @property
    def name(self):
        """
        Gets the name of this Document.
        Name of the document

        :return: The name of this Document.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """
        Sets the name of this Document.
        Name of the document

        :param name: The name of this Document.
        :type: str
        """

        self._name = name

    @property
    def locator(self):
        """
        Gets the locator of this Document.
        URL of the source

        :return: The locator of this Document.
        :rtype: str
        """
        return self._locator

    @locator.setter
    def locator(self, locator):
        """
        Sets the locator of this Document.
        URL of the source

        :param locator: The locator of this Document.
        :type: str
        """

        self._locator = locator

    @property
    def thumbprint(self):
        """
        Gets the thumbprint of this Document.
        SHA-256 thumbprint of object content

        :return: The thumbprint of this Document.
        :rtype: str
        """
        return self._thumbprint

    @thumbprint.setter
    def thumbprint(self, thumbprint):
        """
        Sets the thumbprint of this Document.
        SHA-256 thumbprint of object content

        :param thumbprint: The thumbprint of this Document.
        :type: str
        """

        self._thumbprint = thumbprint

    @property
    def creation_date(self):
        """
        Gets the creation_date of this Document.
        Date invoice record was created

        :return: The creation_date of this Document.
        :rtype: str
        """
        return self._creation_date

    @creation_date.setter
    def creation_date(self, creation_date):
        """
        Sets the creation_date of this Document.
        Date invoice record was created

        :param creation_date: The creation_date of this Document.
        :type: str
        """

        self._creation_date = creation_date

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
        if not isinstance(other, Document):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
