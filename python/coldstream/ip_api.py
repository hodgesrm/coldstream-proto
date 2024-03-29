# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

from enum import Enum
import json
import logging
import os
import uuid

# Our stuff. 
import ip_base
import generated.api.models as models
import docstore
import metastore
import ocr
import validate
import convert
import table.tablebuilder
import table.pfactory

# Define logger
logger = logging.getLogger(__name__)


class InputError(ip_base.BaseError):
    def __init__(self, msg):
        super().__init__(msg)


class Time_Range(Enum):
    """Time range handling for conversions"""
    AS_GIVEN = "AS_GIVEN"
    BY_DAY = "BY_DAY"


class Format(Enum):
    """Conversion output format"""
    CSV = "CSV"
    HTML = "HTML"


class InvoiceApi:
    """Implements invoice operations"""

    def __init__(self, repo):
        self._repo = repo
        if self._repo is None:
            raise InputError("Repo definition is required")
        self._docstore = docstore.DocumentStore(self._repo)
        self._metastore = metastore.MetadataStore(self._repo)
        self._ocr = ocr.Ocr(self._repo)

    def load_invoice(self, source_path, description=None, tags=None):
        """Accept a new invoice document into the document store and return
        an invoice envelope.
        """

        # Start by creating an invoice instance to store information about
        # this particular invoice.
        envelope = models.InvoiceEnvelope(id=str(uuid.uuid4()))
        envelope.description = description
        envelope.tags = tags
        envelope.state = "CREATED"

        # Add the document to the store.
        document = self._store_invoice(source_path)
        envelope.source = document

        # Store in metadata store and return.
        id = self._metastore.put(envelope, id=envelope.id)
        return envelope

    def _store_invoice(self, source_path):
        """Stores an invoice source file and returns a document instance"""
        # Validate arguments. 
        if source_path == None or not os.path.exists(source_path):
            raise InputError(
                "Document source path is missing or unreadable: {0}".format(source_path))

        # Generate document metadata and serialize to JSON.
        metadata = {}
        metadata['source_path'] = source_path
        metadata['name'] = os.path.basename(source_path)

        # Write the file and associated metadata.
        id, sha256 = self._docstore.put(source_path, metadata)

        # Create a document descriptor and return. 
        document = models.Document(id=id, name=metadata['name'],
                                   locator=source_path, thumbprint=sha256)
        return document

    def process_invoice(self, id):
        """Scan and derive semantic model for an invoice.
        """

        # Load the invoice from the metadata store and scan it.
        envelope = self._metastore.get(id, models.InvoiceEnvelope)
        logger.debug("Scanning invoice")
        scan_tmp_path, ocr_scan = self._scan_invoice(envelope.source)
        logger.debug("OCR_SCAN: " + self._dump_to_json(ocr_scan))
        logger.debug("SCAN_TMP_PATH: " + scan_tmp_path)
        envelope.ocr = ocr_scan

        # Store invoice metadata.
        self._metastore.put(envelope, id=envelope.id)

        # Compute tabular model.
        with open(scan_tmp_path, "rb") as xml_file:
            xml = xml_file.read()
        tabular_model = table.tablebuilder.build_model(xml)
        logger.debug("TABULAR MODEL: " + self._dump_to_json(tabular_model))

        # Find a provider.
        provider = table.pfactory.get_provider(tabular_model)
        if provider is None:
            raise InputError("Can't find a provider!!!")
        else:
            logger.info("PROVIDER: " + provider.name())
            content = provider.get_content()
            envelope.content = content
            envelope.state = "INTERPRETED"
            logger.debug("INVOICE CONTENT: " + self._dump_to_json(content))
            self._metastore.put(envelope, id=envelope.id)

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
        descriptor = models.OcrScan(id=id, locator=id, thumbprint=sha256)
        return tmp_path, descriptor

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)

    def get_invoice(self, id):
        """List an invoice document"""
        return self._metastore.get(id, models.InvoiceEnvelope)

    def get_all_invoices(self):
        """List all invoices"""
        return self._metastore.list(models.InvoiceEnvelope)

    def validate_invoice(self, id):
        all_match = True
        invoice = self._metastore.get(id, models.InvoiceEnvelope)
        if invoice is None:
            raise "Invoice not found: id={0}".format(id)

        ruleset = validate.invoice_rule_set()
        for rule in ruleset.get_entity_rules(validate.Rule.INVOICE):
            match, explanation = rule.validate(invoice.content)
            all_match &= match
            logger.debug("Applying rule: name={0} matches={1} explanation={2}".format(
                rule.name, match, explanation))
            if match is False:
                print("Invoice rule failed:")
                print("  Name: {0}".format(rule.name))
                print("  Description: {0}".format(rule.description))
                print("  Explanation: {0}".format(explanation))

        for item in invoice.content.items:
            for rule in ruleset.get_entity_rules(validate.Rule.INVOICE_ITEM):
                match, explanation = rule.validate(item)
                all_match &= match
                logger.debug("Applying rule: name={0} matches={1} explanation={2}".format(
                    rule.name, match, explanation))
                if match is False:
                    print("Invoice rule failed:")
                    print("  Item ID: {0}".format(item.item_id))
                    print("  Resource ID: {0}".format(item.resource_id))
                    print("  Name:        {0}".format(rule.name))
                    print("  Description: {0}".format(rule.description))
                    print("  Explanation: {0}".format(explanation))

        return all_match

    def convert_invoice(self, id, format=Format.CSV, time_range=Time_Range.AS_GIVEN):
        all_match = True
        invoice = self._metastore.get(id, models.InvoiceEnvelope)
        if invoice is None:
            raise "Invoice envelope not found: id={0}".format(id)

        if format == Format.CSV:
            output_generator = convert.csv_output_generator
        else:
            raise InputError("Invalid conversion output format: {0}".format(format))

        if time_range == Time_Range.AS_GIVEN:
            data_generator = convert.invoice_items_generator
        elif time_range == Time_Range.BY_DAY:
            data_generator = convert.invoice_items_daily_generator

        for output_line in output_generator(invoice.content, data_generator):
            print(output_line)

    def delete_invoice(self, id):
        """Delete an invoice document"""
        if id == None:
            raise InputError("Invoice id is missing")
        envelope = self.get_invoice(id)
        # Clear storage if it exists.
        if envelope.source is not None:
            self._docstore.delete(envelope.source.id)
        if envelope.ocr is not None:
            self._docstore.delete(envelope.ocr.id)
        self._metastore.delete(id, models.InvoiceEnvelope)
