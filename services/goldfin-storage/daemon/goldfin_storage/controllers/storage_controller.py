import connexion
import logging
import flask
from goldfin_storage import s3
from goldfin_storage.models.api_response import ApiResponse
from goldfin_storage.models.document import Document
from goldfin_storage.models.tenant_info import TenantInfo
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime

# Our logger.
logger = logging.getLogger(__name__)


def tenant_create(tenant=None):
    """
    Create storage folder for tenant
    Create storage folder for tenant, failing if the folder exists
    :param tenant: tenant information
    :type tenant: TenantInfo

    :rtype: None
    """
    # if connexion.request.is_json:
    #    tenant = TenantInfo.from_dict(connexion.request.get_json())
    logging.info("Create tenant: {0}".format(str(tenant)))
    if tenant is None:
        flask.abort(400, "Missing tenant definition")

    # OK, now we need to create the tenant definition.
    server = s3.initializeFromEnvironmental()
    return server.tenant_create(tenant)


def tenant_delete(tenantId):
    """
    Delete a tenant
    Delete a tenant
    :param tenantId: Document ID
    :type tenantId: str

    :rtype: None
    """
    server = s3.initializeFromEnvironmental()
    server.tenant_delete(tenantId)


def tenant_document_content(tenantId, id):
    """
    Download document content
    Return binary data
    :param tenantId: Tenant ID
    :type tenantId: str
    :param id: Document ID
    :type id: str

    :rtype: file
    """
    server = s3.initializeFromEnvironmental()
    return server.tenant_document_content(tenantId, id)


def tenant_document_create(tenantId, file, name=None, description=None):
    """
    Create a new document for tenant
    Upload a new document including metadata
    :param tenantId: Tenant ID
    :type tenantId: str
    :param file: Document file
    :type file: werkzeug.datastructures.FileStorage
    :param name: Document file name
    :type name: str
    :param description: Description of the document
    :type description: str

    :rtype: Document
    """
    server = s3.initializeFromEnvironmental()
    return server.tenant_document_create(tenantId, file.stream, name, description, file.content_length, file.content_type)


def tenant_document_delete(tenantId, id):
    """
    Delete a document
    Delete a single document
    :param tenantId: Document ID
    :type tenantId: str
    :param id: Document ID
    :type id: str

    :rtype: None
    """
    s3.s3server.tenant_delete_document(tenantId, id)


def tenant_document_metadata(tenantId, id):
    """
    Show document metadata
    Return document metadadata
    :param tenantId: Tenant ID
    :type tenantId: str
    :param id: Document ID
    :type id: str

    :rtype: Document
    """
    return 'do some magic!'


def tenant_document_showall(tenantId):
    """
    List tenant documents
    Show tenant document metadata
    :param tenantId: Tenant ID
    :type tenantId: str

    :rtype: List[Document]
    """
    return 'do some magic!'


def tenant_show(tenantId):
    """
    Show tenant metadata
    Show tenant metadata
    :param tenantId: Tenant ID
    :type tenantId: str

    :rtype: TenantInfo
    """
    server = s3.initializeFromEnvironmental()
    return server.tenant_show(tenantId)


def tenant_show_all():
    """
    List tenant storage folder
    Return a list of tenant folders

    :rtype: List[TenantInfo]
    """
    return 'do some magic!'
