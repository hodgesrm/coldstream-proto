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


class Tenant(object):
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
        'schema_suffix': 'str',
        'description': 'str',
        'state': 'str',
        'creation_date': 'str'
    }

    attribute_map = {
        'id': 'id',
        'name': 'name',
        'schema_suffix': 'schema_suffix',
        'description': 'description',
        'state': 'state',
        'creation_date': 'creationDate'
    }

    def __init__(self, id=None, name=None, schema_suffix=None, description=None, state=None, creation_date=None):
        """
        Tenant - a model defined in Swagger
        """

        self._id = None
        self._name = None
        self._schema_suffix = None
        self._description = None
        self._state = None
        self._creation_date = None

        if id is not None:
          self.id = id
        if name is not None:
          self.name = name
        if schema_suffix is not None:
          self.schema_suffix = schema_suffix
        if description is not None:
          self.description = description
        if state is not None:
          self.state = state
        if creation_date is not None:
          self.creation_date = creation_date

    @property
    def id(self):
        """
        Gets the id of this Tenant.
        Unique tenant id

        :return: The id of this Tenant.
        :rtype: str
        """
        return self._id

    @id.setter
    def id(self, id):
        """
        Sets the id of this Tenant.
        Unique tenant id

        :param id: The id of this Tenant.
        :type: str
        """

        self._id = id

    @property
    def name(self):
        """
        Gets the name of this Tenant.
        Unique tenant name

        :return: The name of this Tenant.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """
        Sets the name of this Tenant.
        Unique tenant name

        :param name: The name of this Tenant.
        :type: str
        """

        self._name = name

    @property
    def schema_suffix(self):
        """
        Gets the schema_suffix of this Tenant.
        Tenant schema suffix

        :return: The schema_suffix of this Tenant.
        :rtype: str
        """
        return self._schema_suffix

    @schema_suffix.setter
    def schema_suffix(self, schema_suffix):
        """
        Sets the schema_suffix of this Tenant.
        Tenant schema suffix

        :param schema_suffix: The schema_suffix of this Tenant.
        :type: str
        """

        self._schema_suffix = schema_suffix

    @property
    def description(self):
        """
        Gets the description of this Tenant.
        Optional information about the tenant

        :return: The description of this Tenant.
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """
        Sets the description of this Tenant.
        Optional information about the tenant

        :param description: The description of this Tenant.
        :type: str
        """

        self._description = description

    @property
    def state(self):
        """
        Gets the state of this Tenant.
        The current processing state of the tenant. 

        :return: The state of this Tenant.
        :rtype: str
        """
        return self._state

    @state.setter
    def state(self, state):
        """
        Sets the state of this Tenant.
        The current processing state of the tenant. 

        :param state: The state of this Tenant.
        :type: str
        """
        allowed_values = ["PENDING", "ENABLED", "DISABLED"]
        if state not in allowed_values:
            raise ValueError(
                "Invalid value for `state` ({0}), must be one of {1}"
                .format(state, allowed_values)
            )

        self._state = state

    @property
    def creation_date(self):
        """
        Gets the creation_date of this Tenant.
        Date invoice record was created

        :return: The creation_date of this Tenant.
        :rtype: str
        """
        return self._creation_date

    @creation_date.setter
    def creation_date(self, creation_date):
        """
        Sets the creation_date of this Tenant.
        Date invoice record was created

        :param creation_date: The creation_date of this Tenant.
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
        if not isinstance(other, Tenant):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
