import connexion
from api.models.api_response import ApiResponse
from api.models.vendor import Vendor
from api.models.vendor_parameters import VendorParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def vendor_create(body):
    """
    Create a new vendor
    Upload a new vendor definition.  Vendors are also created automatically if a vendor invoice is processed.
    :param body: Vendor registration request parameters
    :type body: dict | bytes

    :rtype: Vendor
    """
    if connexion.request.is_json:
        body = VendorParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def vendor_delete(id):
    """
    Delete a vendor
    Delete a single vendor.  This can only be done if the vendor is not attached to invoices or existing inventory.
    :param id: vendor ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def vendor_show(id):
    """
    Show a single vendor
    Return all information relative to a single vendor
    :param id: Vendor ID
    :type id: str

    :rtype: Vendor
    """
    return 'do some magic!'


def vendor_showall():
    """
    List vendors
    Return a list of all vendors

    :rtype: None
    """
    return 'do some magic!'


def vendor_update(id, body=None):
    """
    Update a vendor
    Update vendor description.
    :param id: Vendor ID
    :type id: str
    :param body: Vendor parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = VendorParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'
