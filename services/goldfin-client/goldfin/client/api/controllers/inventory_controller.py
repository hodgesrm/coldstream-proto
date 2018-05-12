import connexion
from api.models.api_response import ApiResponse
from api.models.host import Host
from api.models.observation import Observation
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def host_delete(id):
    """
    Delete host record
    Delete a host record.  It can be recreated by rescanning the corresponding document
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def host_show(id):
    """
    Show single host inventory record
    Returns the most recent inventory record for a specific host.  The host must be identified by the resource ID or internal ID
    :param id: Host resource ID
    :type id: str

    :rtype: Host
    """
    return 'do some magic!'


def host_show_all():
    """
    List current host inventory records
    Return a list of current hosts in inventory.  This returns the most recent record for each host.

    :rtype: List[Host]
    """
    return 'do some magic!'


def observation_create(file):
    """
    Upload observation
    Upload a new observation
    :param file: Observation content
    :type file: werkzeug.datastructures.FileStorage

    :rtype: Observation
    """
    return 'do some magic!'


def observation_delete(id):
    """
    Delete a invoice
    Delete an observation and derived inventory information
    :param id: Observation ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def observation_fetch_content(id):
    """
    Return observation content
    Download observation content serialized to UTF-encoded string
    :param id: Observation ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def observation_process(id):
    """
    Kick off background processing of observation
    Run background processing of observation, which may generate one or more inventory records.
    :param id: Observation ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def observation_show(id):
    """
    Return observation metadata
    Download observation metadata
    :param id: Observation ID
    :type id: str

    :rtype: Observation
    """
    return 'do some magic!'


def observation_show_all():
    """
    List observations
    Return a list of all observations

    :rtype: List[Observation]
    """
    return 'do some magic!'
