import connexion
from api.models.api_response import ApiResponse
from api.models.tenant import Tenant
from api.models.tenant_parameters import TenantParameters
from api.models.tenant_registration_parameters import TenantRegistrationParameters
from api.models.user import User
from api.models.user_parameters import UserParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def tenant_create(body):
    """
    Create a new tenant
    Upload a new tenant registration request.  The tenant will be in a pending state until at least one user is enabled.
    :param body: Tenant registration request parameters
    :type body: dict | bytes

    :rtype: Tenant
    """
    if connexion.request.is_json:
        body = TenantRegistrationParameters.from_dict(connexion.request.get_json())
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


def user_create(tenantId, body):
    """
    Create a new user for a tenant
    Upload a new user registration request.
    :param tenantId: Tenant Id
    :type tenantId: str
    :param body: User registration request parameters
    :type body: dict | bytes

    :rtype: User
    """
    if connexion.request.is_json:
        body = UserParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'


def user_delete(tenantId, id):
    """
    Delete a user
    Delete a user
    :param tenantId: Tenant Id
    :type tenantId: str
    :param id: User ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def user_show(tenantId, id):
    """
    Show a single user
    Return all information relative to a single user
    :param tenantId: Tenant Id
    :type tenantId: str
    :param id: User ID
    :type id: str

    :rtype: Tenant
    """
    return 'do some magic!'


def user_showall(tenantId):
    """
    List users
    Return a list of all users for the tenant
    :param tenantId: Tenant Id
    :type tenantId: str

    :rtype: List[User]
    """
    return 'do some magic!'


def user_update(tenantId, id, body=None):
    """
    Update a user
    Update user description
    :param tenantId: Tenant Id
    :type tenantId: str
    :param id: User ID
    :type id: str
    :param body: User parameters
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = UserParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'
