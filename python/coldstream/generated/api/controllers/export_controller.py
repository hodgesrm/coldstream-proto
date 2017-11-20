import connexion
from api.models.api_response import ApiResponse
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def export_invoice(outputFormat=None, timeSlice=None, filterSpec=None):
    """
    Export invoice data
    Export selected invoice data to useful formats
    :param outputFormat: Output format to generate.  CSV is the only currently allowed value.
    :type outputFormat: str
    :param timeSlice: Optional field to explode values out by a time unit.  Allowed values are DAY, HOUR, MINUTE
    :type timeSlice: str
    :param filterSpec: Predicate list to select invoices.
    :type filterSpec: str

    :rtype: None
    """
    return 'do some magic!'
