import connexion
from api.models.api_response import ApiResponse
from api.models.tenant import Tenant
from api.models.tenant_parameters import TenantParameters
from api.models.user import User
from api.models.user_parameters import UserParameters
from api.models.user_password_parameters import UserPasswordParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def tenant_create(body):
    """
    Create a new tenant
    Upload a new tenant registration request.
    :param body: Tenant creation parameters
    :type body: dict | bytes

    :rtype: Tenant
    """
    if connexion.request.is_json:
        body = TenantParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def tenant_delete(id):
    """
    Delete a tenant
    Delete a single tenant
    :param id: Tenant ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def tenant_show(id):
    """
    Show a single tenant
    Return all information relative to a single tenant
    :param id: Tenant ID
    :type id: str

    :rtype: Tenant
    """
    return 'do some magic!'


def tenant_showall():
    """
    List tenants
    Return a list of all tenants

    :rtype: None
    """
    return 'do some magic!'


def tenant_update(id, body=None):
    """
    Update a tenant
    Update invoice description and tags. Changes to other fields are ignored
    :param id: Tenant ID
    :type id: str
    :param body: Tenant parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = TenantParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def user_create(body):
    """
    Create a new user for a tenant
    Upload a new user registration request.
    :param body: User registration request parameters
    :type body: dict | bytes

    :rtype: User
    """
    if connexion.request.is_json:
        body = UserParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def user_delete(id):
    """
    Delete a user
    Delete a user
    :param id: User ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def user_show(id):
    """
    Show a single user
    Return all information relative to a single user
    :param id: User ID
    :type id: str

    :rtype: Tenant
    """
    return 'do some magic!'


def user_showall():
    """
    List users
    Return a list of all users visible to current user

    :rtype: List[User]
    """
    return 'do some magic!'


def user_update(id, body=None):
    """
    Update a user
    Update user description
    :param id: User ID
    :type id: str
    :param body: User parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = UserParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def user_update_password(id, body=None):
    """
    Update user password
    Sets a new user password
    :param id: User ID
    :type id: str
    :param body: Password change parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = UserPasswordParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'
