import connexion
from api.models.api_response import ApiResponse
from api.models.host import Host
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def inventory_host_create(body):
    """
    Create a new host inventory entry
    Upload a host inventory entry
    :param body: The host definition
    :type body: dict | bytes

    :rtype: Host
    """
    if connexion.request.is_json:
        body = Host.from_dict(connexion.request.get_json())
    return 'do some magic!'


def inventory_host_delete(id):
    """
    Delete a host inventory record
    Delete a host inventory record
    :param id: Host ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def inventory_host_show_all(summary=None):
    """
    List host entries
    Return a list of all host inventory records
    :param summary: If true return complete invoice content, otherwise just the Invoice fields
    :type summary: bool

    :rtype: List[Host]
    """
    return 'do some magic!'


def invoice_host_show(id):
    """
    Show a single host inventory record
    Return all information relative to a single host
    :param id: Host ID
    :type id: str

    :rtype: Host
    """
    return 'do some magic!'
