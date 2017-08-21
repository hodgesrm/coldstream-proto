# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import configparser
import os

# Our stuff. 
import ip_base
import generated.api.models.document
import docstore

class InputError(ip_base.BaseError):
    def __init__(self, msg):
        super().__init__(msg)

class InvoiceApi:
    """Implements invoice operations"""
    def __init__(self, repo):
        self._repo = repo
        if self._repo == None:
            raise Error("Repo definition is required")

    def get_repo(self):
        return self._repo

    def create_invoice(self, source_path, name=None):
        """Accept a new invoice document into the document store and return 
        the document UUID.
        """
        # Validate arguments. 
        if source_path == None or not os.path.exists(source_path):
            raise InputError(
                "Document source path is missing or unreadable: {0}".format(source_path))

        # Generate document metadata and serialize to JSON.
        metadata = {}
        metadata['source_path'] = source_path
        if name == None:
            metadata['name'] = os.path.basename(source_path)
        else:
            metadata['name'] = name

        # Write the file and associated metadata.
        doc_store = docstore.DocumentStore(self._repo)
        id = doc_store.put(source_path, metadata)
        return id

    def get_invoice(self, id):
        """List an invoice document"""
        doc_store = docstore.DocumentStore(self._repo)
        if id == None:
            return doc_store.list()
        else:
            return doc_store.get_metadata(id)

    def delete_invoice(self, id):
        """Delete an invoice document"""
        doc_store = docstore.DocumentStore(self._repo)
        if id == None:
            raise InputError("Document id is missing")
        doc_store.delete(id)
