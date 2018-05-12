import connexion
from api.models.invoice import Invoice
from api.models.invoice_parameters import InvoiceParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def invoice_delete(id):
    """
    Delete an invoice
    Delete an invoice.  It can be recreated by rescanning the corresponding document
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def invoice_show(id, full=None):
    """
    Show a single invoice
    Return all information relative to a single invoice
    :param id: Invoice ID
    :type id: str
    :param full: If true, return full invoices with all line items
    :type full: bool

    :rtype: Invoice
    """
    return 'do some magic!'


def invoice_show_all(full=None):
    """
    List invoices
    Return a list of all invoices
    :param full: If true, return full invoices with all line items
    :type full: bool

    :rtype: List[Invoice]
    """
    return 'do some magic!'


def invoice_update(id, body=None):
    """
    Update an invoice
    Update invoice description and tags. Changes to other fields are ignored
    :param id: Invoice ID
    :type id: str
    :param body: Invoice parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = InvoiceParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def invoice_validate(id):
    """
    Start invoice validations
    Run invoice validations
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'
