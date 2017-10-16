import connexion
from api.models.api_response import ApiResponse
from api.models.invoice_envelope import InvoiceEnvelope
from api.models.invoice_envelope_parameters import InvoiceEnvelopeParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def invoice_create(file, description=None):
    """
    Create a new invoice for logged in tenant
    Upload a new invoice and kick off processing
    :param file: Invoice file
    :type file: werkzeug.datastructures.FileStorage
    :param description: A optional description of the invoice
    :type description: str

    :rtype: InvoiceEnvelope
    """
    return 'do some magic!'


def invoice_delete(id):
    """
    Delete an invoice
    Delete a single
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def invoice_process(id):
    """
    Start invoice processing
    Run background OCR and interpretation on invoice.  The invoice state will be set to CREATED before this call returns.
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def invoice_show(id):
    """
    Show a single invoice
    Return all information relative to a single invoice
    :param id: Invoice ID
    :type id: str

    :rtype: InvoiceEnvelope
    """
    return 'do some magic!'


def invoice_show_all(summary=None):
    """
    List invoices
    Return a list of all invoices
    :param summary: If true return complete invoice content, otherwise just the Invoice fields
    :type summary: bool

    :rtype: List[InvoiceEnvelope]
    """
    return 'do some magic!'


def invoice_update(id, body=None):
    """
    Update an invoice
    Update invoice description and tags. Changes to other fields are ignored
    :param id: Invoice ID
    :type id: str
    :param body: Invoice descriptor
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = InvoiceEnvelopeParameters.from_dict(connexion.request.get_json())
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
