import connexion
from api.models.api_response import ApiResponse
from api.models.login_credentials import LoginCredentials
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
        body = LoginCredentials.from_dict(connexion.request.get_json())
    return 'do some magic!'


def logout(token):
    """
    Logout from system
    Delete session, which is no longer usable after this call
    :param token: Session ID token
    :type token: str

    :rtype: None
    """
    return 'do some magic!'
