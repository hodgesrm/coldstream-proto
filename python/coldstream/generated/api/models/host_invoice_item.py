# coding: utf-8

"""
    Coldstream Prototype

    Coldstream model for invoice processing. 

    OpenAPI spec version: 1.0.0
    Contact: rhodges@skylineresearch.comm
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from pprint import pformat
from six import iteritems
import re


class HostInvoiceItem(object):
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
        'item_id': 'str',
        'resource_id': 'str',
        'unit_amount': 'float',
        'units': 'int',
        'total_amount': 'float',
        'currency': 'str',
        'start_date': 'str',
        'end_date': 'str',
        'invoice_region': 'DocumentRegion',
        'host': 'Host'
    }

    attribute_map = {
        'item_id': 'itemId',
        'resource_id': 'resourceId',
        'unit_amount': 'unitAmount',
        'units': 'units',
        'total_amount': 'totalAmount',
        'currency': 'currency',
        'start_date': 'startDate',
        'end_date': 'endDate',
        'invoice_region': 'invoiceRegion',
        'host': 'host'
    }

    def __init__(self, item_id=None, resource_id=None, unit_amount=None, units=None, total_amount=None, currency=None, start_date=None, end_date=None, invoice_region=None, host=None):
        """
        HostInvoiceItem - a model defined in Swagger
        """

        self._item_id = None
        self._resource_id = None
        self._unit_amount = None
        self._units = None
        self._total_amount = None
        self._currency = None
        self._start_date = None
        self._end_date = None
        self._invoice_region = None
        self._host = None

        if item_id is not None:
          self.item_id = item_id
        if resource_id is not None:
          self.resource_id = resource_id
        if unit_amount is not None:
          self.unit_amount = unit_amount
        if units is not None:
          self.units = units
        if total_amount is not None:
          self.total_amount = total_amount
        if currency is not None:
          self.currency = currency
        if start_date is not None:
          self.start_date = start_date
        if end_date is not None:
          self.end_date = end_date
        if invoice_region is not None:
          self.invoice_region = invoice_region
        if host is not None:
          self.host = host

    @property
    def item_id(self):
        """
        Gets the item_id of this HostInvoiceItem.
        Invoice item ID if specified

        :return: The item_id of this HostInvoiceItem.
        :rtype: str
        """
        return self._item_id

    @item_id.setter
    def item_id(self, item_id):
        """
        Sets the item_id of this HostInvoiceItem.
        Invoice item ID if specified

        :param item_id: The item_id of this HostInvoiceItem.
        :type: str
        """

        self._item_id = item_id

    @property
    def resource_id(self):
        """
        Gets the resource_id of this HostInvoiceItem.
        Inventory resource ID

        :return: The resource_id of this HostInvoiceItem.
        :rtype: str
        """
        return self._resource_id

    @resource_id.setter
    def resource_id(self, resource_id):
        """
        Sets the resource_id of this HostInvoiceItem.
        Inventory resource ID

        :param resource_id: The resource_id of this HostInvoiceItem.
        :type: str
        """

        self._resource_id = resource_id

    @property
    def unit_amount(self):
        """
        Gets the unit_amount of this HostInvoiceItem.
        Cost per unit

        :return: The unit_amount of this HostInvoiceItem.
        :rtype: float
        """
        return self._unit_amount

    @unit_amount.setter
    def unit_amount(self, unit_amount):
        """
        Sets the unit_amount of this HostInvoiceItem.
        Cost per unit

        :param unit_amount: The unit_amount of this HostInvoiceItem.
        :type: float
        """

        self._unit_amount = unit_amount

    @property
    def units(self):
        """
        Gets the units of this HostInvoiceItem.
        Number of units

        :return: The units of this HostInvoiceItem.
        :rtype: int
        """
        return self._units

    @units.setter
    def units(self, units):
        """
        Sets the units of this HostInvoiceItem.
        Number of units

        :param units: The units of this HostInvoiceItem.
        :type: int
        """

        self._units = units

    @property
    def total_amount(self):
        """
        Gets the total_amount of this HostInvoiceItem.
        Total cost for all units

        :return: The total_amount of this HostInvoiceItem.
        :rtype: float
        """
        return self._total_amount

    @total_amount.setter
    def total_amount(self, total_amount):
        """
        Sets the total_amount of this HostInvoiceItem.
        Total cost for all units

        :param total_amount: The total_amount of this HostInvoiceItem.
        :type: float
        """

        self._total_amount = total_amount

    @property
    def currency(self):
        """
        Gets the currency of this HostInvoiceItem.
        Item currency

        :return: The currency of this HostInvoiceItem.
        :rtype: str
        """
        return self._currency

    @currency.setter
    def currency(self, currency):
        """
        Sets the currency of this HostInvoiceItem.
        Item currency

        :param currency: The currency of this HostInvoiceItem.
        :type: str
        """

        self._currency = currency

    @property
    def start_date(self):
        """
        Gets the start_date of this HostInvoiceItem.
        Begining of the time range

        :return: The start_date of this HostInvoiceItem.
        :rtype: str
        """
        return self._start_date

    @start_date.setter
    def start_date(self, start_date):
        """
        Sets the start_date of this HostInvoiceItem.
        Begining of the time range

        :param start_date: The start_date of this HostInvoiceItem.
        :type: str
        """

        self._start_date = start_date

    @property
    def end_date(self):
        """
        Gets the end_date of this HostInvoiceItem.
        End of the time range

        :return: The end_date of this HostInvoiceItem.
        :rtype: str
        """
        return self._end_date

    @end_date.setter
    def end_date(self, end_date):
        """
        Sets the end_date of this HostInvoiceItem.
        End of the time range

        :param end_date: The end_date of this HostInvoiceItem.
        :type: str
        """

        self._end_date = end_date

    @property
    def invoice_region(self):
        """
        Gets the invoice_region of this HostInvoiceItem.

        :return: The invoice_region of this HostInvoiceItem.
        :rtype: DocumentRegion
        """
        return self._invoice_region

    @invoice_region.setter
    def invoice_region(self, invoice_region):
        """
        Sets the invoice_region of this HostInvoiceItem.

        :param invoice_region: The invoice_region of this HostInvoiceItem.
        :type: DocumentRegion
        """

        self._invoice_region = invoice_region

    @property
    def host(self):
        """
        Gets the host of this HostInvoiceItem.

        :return: The host of this HostInvoiceItem.
        :rtype: Host
        """
        return self._host

    @host.setter
    def host(self, host):
        """
        Sets the host of this HostInvoiceItem.

        :param host: The host of this HostInvoiceItem.
        :type: Host
        """

        self._host = host

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
        if not isinstance(other, HostInvoiceItem):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
