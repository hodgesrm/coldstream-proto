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


class Invoice(object):
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
        'document_id': 'str',
        'description': 'str',
        'tags': 'str',
        'identifier': 'str',
        'effective_date': 'datetime',
        'vendor': 'str',
        'subtotal_amount': 'float',
        'tax': 'float',
        'total_amount': 'float',
        'currency': 'str',
        'items': 'List[InvoiceItem]',
        'creation_date': 'str'
    }

    attribute_map = {
        'id': 'id',
        'document_id': 'documentId',
        'description': 'description',
        'tags': 'tags',
        'identifier': 'identifier',
        'effective_date': 'effectiveDate',
        'vendor': 'vendor',
        'subtotal_amount': 'subtotalAmount',
        'tax': 'tax',
        'total_amount': 'totalAmount',
        'currency': 'currency',
        'items': 'items',
        'creation_date': 'creationDate'
    }

    def __init__(self, id=None, document_id=None, description=None, tags=None, identifier=None, effective_date=None, vendor=None, subtotal_amount=None, tax=None, total_amount=None, currency=None, items=None, creation_date=None):
        """
        Invoice - a model defined in Swagger
        """

        self._id = None
        self._document_id = None
        self._description = None
        self._tags = None
        self._identifier = None
        self._effective_date = None
        self._vendor = None
        self._subtotal_amount = None
        self._tax = None
        self._total_amount = None
        self._currency = None
        self._items = None
        self._creation_date = None

        if id is not None:
          self.id = id
        if document_id is not None:
          self.document_id = document_id
        if description is not None:
          self.description = description
        if tags is not None:
          self.tags = tags
        if identifier is not None:
          self.identifier = identifier
        if effective_date is not None:
          self.effective_date = effective_date
        if vendor is not None:
          self.vendor = vendor
        if subtotal_amount is not None:
          self.subtotal_amount = subtotal_amount
        if tax is not None:
          self.tax = tax
        if total_amount is not None:
          self.total_amount = total_amount
        if currency is not None:
          self.currency = currency
        if items is not None:
          self.items = items
        if creation_date is not None:
          self.creation_date = creation_date

    @property
    def id(self):
        """
        Gets the id of this Invoice.
        Invoice ID

        :return: The id of this Invoice.
        :rtype: str
        """
        return self._id

    @id.setter
    def id(self, id):
        """
        Sets the id of this Invoice.
        Invoice ID

        :param id: The id of this Invoice.
        :type: str
        """

        self._id = id

    @property
    def document_id(self):
        """
        Gets the document_id of this Invoice.
        Source document ID

        :return: The document_id of this Invoice.
        :rtype: str
        """
        return self._document_id

    @document_id.setter
    def document_id(self, document_id):
        """
        Sets the document_id of this Invoice.
        Source document ID

        :param document_id: The document_id of this Invoice.
        :type: str
        """

        self._document_id = document_id

    @property
    def description(self):
        """
        Gets the description of this Invoice.
        A user-provided description of the invoice

        :return: The description of this Invoice.
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """
        Sets the description of this Invoice.
        A user-provided description of the invoice

        :param description: The description of this Invoice.
        :type: str
        """

        self._description = description

    @property
    def tags(self):
        """
        Gets the tags of this Invoice.
        A user-provided list of name-value pairs that describe the invoice

        :return: The tags of this Invoice.
        :rtype: str
        """
        return self._tags

    @tags.setter
    def tags(self, tags):
        """
        Sets the tags of this Invoice.
        A user-provided list of name-value pairs that describe the invoice

        :param tags: The tags of this Invoice.
        :type: str
        """

        self._tags = tags

    @property
    def identifier(self):
        """
        Gets the identifier of this Invoice.
        Invoice identifier

        :return: The identifier of this Invoice.
        :rtype: str
        """
        return self._identifier

    @identifier.setter
    def identifier(self, identifier):
        """
        Sets the identifier of this Invoice.
        Invoice identifier

        :param identifier: The identifier of this Invoice.
        :type: str
        """

        self._identifier = identifier

    @property
    def effective_date(self):
        """
        Gets the effective_date of this Invoice.
        Invoice effective date

        :return: The effective_date of this Invoice.
        :rtype: datetime
        """
        return self._effective_date

    @effective_date.setter
    def effective_date(self, effective_date):
        """
        Sets the effective_date of this Invoice.
        Invoice effective date

        :param effective_date: The effective_date of this Invoice.
        :type: datetime
        """

        self._effective_date = effective_date

    @property
    def vendor(self):
        """
        Gets the vendor of this Invoice.
        Vendor key

        :return: The vendor of this Invoice.
        :rtype: str
        """
        return self._vendor

    @vendor.setter
    def vendor(self, vendor):
        """
        Sets the vendor of this Invoice.
        Vendor key

        :param vendor: The vendor of this Invoice.
        :type: str
        """

        self._vendor = vendor

    @property
    def subtotal_amount(self):
        """
        Gets the subtotal_amount of this Invoice.
        Invoice subtotal amount

        :return: The subtotal_amount of this Invoice.
        :rtype: float
        """
        return self._subtotal_amount

    @subtotal_amount.setter
    def subtotal_amount(self, subtotal_amount):
        """
        Sets the subtotal_amount of this Invoice.
        Invoice subtotal amount

        :param subtotal_amount: The subtotal_amount of this Invoice.
        :type: float
        """

        self._subtotal_amount = subtotal_amount

    @property
    def tax(self):
        """
        Gets the tax of this Invoice.
        Invoice tax amount

        :return: The tax of this Invoice.
        :rtype: float
        """
        return self._tax

    @tax.setter
    def tax(self, tax):
        """
        Sets the tax of this Invoice.
        Invoice tax amount

        :param tax: The tax of this Invoice.
        :type: float
        """

        self._tax = tax

    @property
    def total_amount(self):
        """
        Gets the total_amount of this Invoice.
        Invoice total (subtotal + tax)

        :return: The total_amount of this Invoice.
        :rtype: float
        """
        return self._total_amount

    @total_amount.setter
    def total_amount(self, total_amount):
        """
        Sets the total_amount of this Invoice.
        Invoice total (subtotal + tax)

        :param total_amount: The total_amount of this Invoice.
        :type: float
        """

        self._total_amount = total_amount

    @property
    def currency(self):
        """
        Gets the currency of this Invoice.
        Currency type

        :return: The currency of this Invoice.
        :rtype: str
        """
        return self._currency

    @currency.setter
    def currency(self, currency):
        """
        Sets the currency of this Invoice.
        Currency type

        :param currency: The currency of this Invoice.
        :type: str
        """

        self._currency = currency

    @property
    def items(self):
        """
        Gets the items of this Invoice.
        Invoice items

        :return: The items of this Invoice.
        :rtype: List[InvoiceItem]
        """
        return self._items

    @items.setter
    def items(self, items):
        """
        Sets the items of this Invoice.
        Invoice items

        :param items: The items of this Invoice.
        :type: List[InvoiceItem]
        """

        self._items = items

    @property
    def creation_date(self):
        """
        Gets the creation_date of this Invoice.
        Date user record was created

        :return: The creation_date of this Invoice.
        :rtype: str
        """
        return self._creation_date

    @creation_date.setter
    def creation_date(self, creation_date):
        """
        Sets the creation_date of this Invoice.
        Date user record was created

        :param creation_date: The creation_date of this Invoice.
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
        if not isinstance(other, Invoice):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
