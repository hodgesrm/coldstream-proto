import connexion
from api.models.api_response import ApiResponse
from api.models.login_request import LoginRequest
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def login_by_credentials(body):
    """
    Login to system
    Obtain API key using login credentials
    :param body: Login credentials
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = LoginRequest.from_dict(connexion.request.get_json())
    return 'do some magic!'
