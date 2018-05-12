import connexion
from api.models.api_response import ApiResponse
from api.models.document import Document
from api.models.document_parameters import DocumentParameters
from datetime import date, datetime
from typing import List, Dict
from six import iteritems
from ..util import deserialize_date, deserialize_datetime


def document_create(file, description=None, scan=None):
    """
    Upload document
    Upload a new document for scanning
    :param file: Document file
    :type file: werkzeug.datastructures.FileStorage
    :param description: A optional description of the document
    :type description: str
    :param scan: Flag to control scanning
    :type scan: bool

    :rtype: Document
    """
    return 'do some magic!'


def document_delete(id):
    """
    Delete a invoice
    Delete a single document and associated semantic content
    :param id: Invoice ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def document_fetch_content(id):
    """
    Return document content
    Download document content
    :param id: Document ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def document_scan(id):
    """
    Kick off document scanning
    Run background scanning on document.  The document state and semantic information will be updated when finished.
    :param id: Document ID
    :type id: str

    :rtype: None
    """
    return 'do some magic!'


def document_show(id):
    """
    Return document metadata
    Download document content
    :param id: Document ID
    :type id: str

    :rtype: Document
    """
    return 'do some magic!'


def document_show_all():
    """
    List documents
    Return a list of all documents

    :rtype: List[Document]
    """
    return 'do some magic!'


def document_update(id, body=None):
    """
    Update document information
    Update document description and tags. Changes to other fields are ignored
    :param id: Document ID
    :type id: str
    :param body: Document descriptor
    :type body: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        body = DocumentParameters.from_dict(connexion.request.get_json())
    return 'do some magic!'
