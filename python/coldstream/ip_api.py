# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import configparser
import os
import uuid

# Our stuff. 
import ip_base
import generated.api.models as models
import docstore
import metastore
import ocr

class InputError(ip_base.BaseError):
    def __init__(self, msg):
        super().__init__(msg)

class InvoiceApi:
    """Implements invoice operations"""
    def __init__(self, repo):
        self._repo = repo
        if self._repo == None:
            raise Error("Repo definition is required")
        self._docstore = docstore.DocumentStore(self._repo)
        self._metastore = metastore.MetadataStore(self._repo)
        self._ocr = ocr.Ocr(self._repo)

    def get_repo(self):
        return self._repo

    def create_invoice(self, source_path, name=None):
        """Accept a new invoice document into the document store and return 
        the invoice UUID.
        """

        # Start by creating an invoice instance to store information about
        # this particular invoice. 
        invoice = models.Invoice(id=str(uuid.uuid4()))

        # Load the document and add information to the invoice. 
        document = self._store_invoice(source_path, name)
        invoice.document = document

        # Scan the invoice. 
        ocr_scan = self._scan_invoice(document)
        invoice.ocr = ocr_scan

        # Store invoice metadata and return ID.
        id = self._metastore.put(invoice, id=invoice.id)
        return id

    def _store_invoice(self, source_path, name):
        """Stores an invoice and returns a descriptor instance"""
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
        id, sha256 = self._docstore.put(source_path, metadata)
 
        # Create a document descriptor and return. 
        descriptor = models.Document(id=id, name=metadata['name'], 
            locator=source_path, thumbprint=sha256)
        return descriptor

    def _scan_invoice(self, document):
        """Scans an invoice"""
        # Scan the invoice. 
        tmp_path = "/tmp/" + document.name + ".xml." + str(uuid.uuid4())
        storage_path = self._docstore.get_storage_path(document.id)
        self._ocr.scan(storage_path, tmp_path)

        # Generate scan metadata and serialize to JSON.
        metadata = {}
        metadata['source_path'] = tmp_path
        metadata['type'] = "invoice_scan"
        metadata['invoice_id'] = document.id

        # Write the file and associated metadata.
        id, sha256 = self._docstore.put(tmp_path, metadata)
        os.remove(tmp_path)
 
        # Create a document descriptor and return. 
        descriptor = models.OcrScan(id=id, doc_id=document.id, 
            locator=document.locator, thumbprint=sha256)
        return descriptor

    def get_invoice(self, id):
        """List an invoice document"""
        if id == None:
            return self._metastore.list(models.Invoice)
        else:
            return self._metastore.get(id, models.Invoice)

    def delete_invoice(self, id):
        """Delete an invoice document"""
        if id == None:
            raise InputError("Invoice id is missing")
        invoice = self.get_invoice(id)
        # Clear storage if it exists.
        if (invoice.document):
            self._docstore.delete(invoice.document.id)
        if (invoice.ocr):
            self._docstore.delete(invoice.ocr.id)
        self._metastore.delete(id, models.Invoice)
