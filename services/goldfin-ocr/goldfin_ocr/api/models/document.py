# coding: utf-8

"""
    Goldfin Service Admin API

    REST API for Goldfin Service Administration

    OpenAPI spec version: 1.0.0
    Contact: info@goldfin.io
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
        'description': 'str',
        'tags': 'str',
        'content_type': 'str',
        'content_length': 'float',
        'thumbprint': 'str',
        'locator': 'str',
        'state': 'str',
        'semantic_type': 'str',
        'semantic_id': 'str',
        'creation_date': 'str'
    }

    attribute_map = {
        'id': 'id',
        'name': 'name',
        'description': 'description',
        'tags': 'tags',
        'content_type': 'contentType',
        'content_length': 'contentLength',
        'thumbprint': 'thumbprint',
        'locator': 'locator',
        'state': 'state',
        'semantic_type': 'semanticType',
        'semantic_id': 'semanticId',
        'creation_date': 'creationDate'
    }

    def __init__(self, id=None, name=None, description=None, tags=None, content_type=None, content_length=None, thumbprint=None, locator=None, state=None, semantic_type=None, semantic_id=None, creation_date=None):
        """
        Document - a model defined in Swagger
        """

        self._id = None
        self._name = None
        self._description = None
        self._tags = None
        self._content_type = None
        self._content_length = None
        self._thumbprint = None
        self._locator = None
        self._state = None
        self._semantic_type = None
        self._semantic_id = None
        self._creation_date = None

        if id is not None:
          self.id = id
        if name is not None:
          self.name = name
        if description is not None:
          self.description = description
        if tags is not None:
          self.tags = tags
        if content_type is not None:
          self.content_type = content_type
        if content_length is not None:
          self.content_length = content_length
        if thumbprint is not None:
          self.thumbprint = thumbprint
        if locator is not None:
          self.locator = locator
        if state is not None:
          self.state = state
        if semantic_type is not None:
          self.semantic_type = semantic_type
        if semantic_id is not None:
          self.semantic_id = semantic_id
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
    def description(self):
        """
        Gets the description of this Document.
        Optional description of the document

        :return: The description of this Document.
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """
        Sets the description of this Document.
        Optional description of the document

        :param description: The description of this Document.
        :type: str
        """

        self._description = description

    @property
    def tags(self):
        """
        Gets the tags of this Document.
        A user-provided list of name-value pairs that describe the invoice

        :return: The tags of this Document.
        :rtype: str
        """
        return self._tags

    @tags.setter
    def tags(self, tags):
        """
        Sets the tags of this Document.
        A user-provided list of name-value pairs that describe the invoice

        :param tags: The tags of this Document.
        :type: str
        """

        self._tags = tags

    @property
    def content_type(self):
        """
        Gets the content_type of this Document.
        Internet media type (e.g., application/octet-stream)

        :return: The content_type of this Document.
        :rtype: str
        """
        return self._content_type

    @content_type.setter
    def content_type(self, content_type):
        """
        Sets the content_type of this Document.
        Internet media type (e.g., application/octet-stream)

        :param content_type: The content_type of this Document.
        :type: str
        """

        self._content_type = content_type

    @property
    def content_length(self):
        """
        Gets the content_length of this Document.
        Document length in bytes

        :return: The content_length of this Document.
        :rtype: float
        """
        return self._content_length

    @content_length.setter
    def content_length(self, content_length):
        """
        Sets the content_length of this Document.
        Document length in bytes

        :param content_length: The content_length of this Document.
        :type: float
        """

        self._content_length = content_length

    @property
    def thumbprint(self):
        """
        Gets the thumbprint of this Document.
        SHA-256 thumbprint of document content

        :return: The thumbprint of this Document.
        :rtype: str
        """
        return self._thumbprint

    @thumbprint.setter
    def thumbprint(self, thumbprint):
        """
        Sets the thumbprint of this Document.
        SHA-256 thumbprint of document content

        :param thumbprint: The thumbprint of this Document.
        :type: str
        """

        self._thumbprint = thumbprint

    @property
    def locator(self):
        """
        Gets the locator of this Document.
        Storage locator of the document

        :return: The locator of this Document.
        :rtype: str
        """
        return self._locator

    @locator.setter
    def locator(self, locator):
        """
        Sets the locator of this Document.
        Storage locator of the document

        :param locator: The locator of this Document.
        :type: str
        """

        self._locator = locator

    @property
    def state(self):
        """
        Gets the state of this Document.
        The current processing state of the document.  The document type and content ID are available after scanning

        :return: The state of this Document.
        :rtype: str
        """
        return self._state

    @state.setter
    def state(self, state):
        """
        Sets the state of this Document.
        The current processing state of the document.  The document type and content ID are available after scanning

        :param state: The state of this Document.
        :type: str
        """
        allowed_values = ["CREATED", "SCAN_REQUESTED", "SCANNED", "ERROR"]
        if state not in allowed_values:
            raise ValueError(
                "Invalid value for `state` ({0}), must be one of {1}"
                .format(state, allowed_values)
            )

        self._state = state

    @property
    def semantic_type(self):
        """
        Gets the semantic_type of this Document.
        Kind of document, e.g., an invoice.

        :return: The semantic_type of this Document.
        :rtype: str
        """
        return self._semantic_type

    @semantic_type.setter
    def semantic_type(self, semantic_type):
        """
        Sets the semantic_type of this Document.
        Kind of document, e.g., an invoice.

        :param semantic_type: The semantic_type of this Document.
        :type: str
        """
        allowed_values = ["INVOICE", "UNKNOWN"]
        if semantic_type not in allowed_values:
            raise ValueError(
                "Invalid value for `semantic_type` ({0}), must be one of {1}"
                .format(semantic_type, allowed_values)
            )

        self._semantic_type = semantic_type

    @property
    def semantic_id(self):
        """
        Gets the semantic_id of this Document.
        ID of the document's scanned content, e.g. an invoice ID

        :return: The semantic_id of this Document.
        :rtype: str
        """
        return self._semantic_id

    @semantic_id.setter
    def semantic_id(self, semantic_id):
        """
        Sets the semantic_id of this Document.
        ID of the document's scanned content, e.g. an invoice ID

        :param semantic_id: The semantic_id of this Document.
        :type: str
        """

        self._semantic_id = semantic_id

    @property
    def creation_date(self):
        """
        Gets the creation_date of this Document.
        Date document was uploaded

        :return: The creation_date of this Document.
        :rtype: str
        """
        return self._creation_date

    @creation_date.setter
    def creation_date(self, creation_date):
        """
        Sets the creation_date of this Document.
        Date document was uploaded

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
