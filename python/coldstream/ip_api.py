# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import json
import os
import uuid

# Our stuff. 
import ip_base
import generated.api.models as models
import docstore
import metastore
import ocr
import table.tablebuilder
import table.pfactory

class InputError(ip_base.BaseError):
    def __init__(self, msg):
        super().__init__(msg)


class InvoiceApi:
    """Implements invoice operations"""

    def __init__(self, repo):
        self._repo = repo
        if self._repo is None:
            raise InputError("Repo definition is required")
        self._docstore = docstore.DocumentStore(self._repo)
        self._metastore = metastore.MetadataStore(self._repo)
        self._ocr = ocr.Ocr(self._repo)

    def load_invoice(self, source_path, name=None):
        """Accept a new invoice document into the document store and return
        the invoice UUID.
        """

        # Start by creating an invoice instance to store information about
        # this particular invoice.
        invoice = models.Invoice(id=str(uuid.uuid4()))

        # Add the document to the store.
        document = self._store_invoice(source_path, name)
        invoice.document = document

        # Store in metadata store and return.
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

    def process_invoice(self, id):
        """Scan and derive semantic model for an invoice.
        """

        # Load the invoice from the metadata store and scan it.
        invoice = self._metastore.get(id, models.Invoice)
        print("Scanning invoice")
        scan_tmp_path, ocr_scan = self._scan_invoice(invoice.document)
        print("OCR_SCAN: " + self._dump_to_json(ocr_scan))
        print("SCAN_TMP_PATH: " + scan_tmp_path)
        invoice.ocr = ocr_scan

        # Store invoice metadata.
        self._metastore.put(invoice, id=invoice.id)

        # Compute tabular model.
        with open(scan_tmp_path, "rb") as xml_file:
            xml = xml_file.read()
        tabular_model = table.tablebuilder.build_model(xml)
        print("TABULAR MODEL: " + self._dump_to_json(tabular_model))

        # Find a provider.
        provider = table.pfactory.get_provider(tabular_model)
        if provider is None:
            print("Can't find a provider!!!")
        else:
            print("PROVIDER: " + provider.name())
            content = provider.get_content()
            invoice.content = invoice
            print("INVOICE CONTENT: " + self._dump_to_json(content))
            self._metastore.put(invoice, id=invoice.id)

        # Remove no matter what.
        os.remove(scan_tmp_path)

    def _scan_invoice(self, document):
        """Scans an invoice"""
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

        # Create a document descriptor and return. 
        descriptor = models.OcrScan(id=id, doc_id=document.id,
                                    locator=id, thumbprint=sha256)
        return tmp_path, descriptor

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)

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