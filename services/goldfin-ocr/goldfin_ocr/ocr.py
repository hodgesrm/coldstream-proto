# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Implementation for OCR processing"""

from ABBYY import CloudOCR
import json
import logging
import tempfile

from .util import sha256
from .s3 import S3Connection, S3Ref
from .table.tablebuilder import build_model
from .table.pfactory import get_provider
from .api.models.document import Document

# Standard logging initialization.
logger = logging.getLogger(__name__)


class OcrProcessor:
    """Submits files to OCR for scanning"""

    def __init__(self, ocr_config):
        """Set up instance

        :param ocr_config: OCR service configuration parameters
        :type ocr_config: dict
        """
        self._ocr_config = ocr_config

    def scan(self, tenant_id, doc, use_cache=True):
        """Scan a file returning a document containing the scan result

        :param tenant_id: Tenant to whom doc belongs
        :type tenant_id: str
        :param doc: Definition of document to be scanned
        :type doc: Document
        :param use_cache: If true cached results are OK for use
        :type use_cache: bool
        :returns Invoice object or None
        """
        logger.info("Processing scan on document: tenant_id={0}, document={1}"
                    .format(tenant_id, doc))

        # Check for a cached result.  If not found, download from S3.
        cached_ocr_file = self._fetch_from_cache(doc, use_cache)
        if cached_ocr_file is None:
            logger.info("Running document OCR: ref={0}".format(doc.locator))
            scan_input = self._fetch_from_s3(doc.locator, doc.thumbprint)
            scan_result_path = self._run_ocr(scan_input)
            if use_cache:
                # Caching the result avoids rerunning OCR on the same file.
                self._load_to_cache(scan_result_path, doc.thumbprint, doc.locator)
        else:
            logger.info("Using cached OCR scan: ref={0}".format(doc.locator))
            scan_result_path = cached_ocr_file.name

        logger.info("Path location of scan data: {0}".format(scan_result_path))

        # Compute tabular model.
        with open(scan_result_path, "rb") as xml_file:
            xml = xml_file.read()
        tabular_model = build_model(xml)
        logger.debug("TABULAR MODEL: " + self._dump_to_json(tabular_model))

        # Find a provider and translate to Invoice model.
        provider = get_provider(tabular_model)
        if provider is None:
            raise Exception("Can't find a provider!!!")
        else:
            logger.info("PROVIDER: " + provider.name())
            invoice = provider.get_content()
            logger.debug("INVOICE CONTENT: " + self._dump_to_json(invoice))
            return invoice

        # Remove no matter what.
        #os.remove(scan_tmp_path)

    def _fetch_from_cache(self, doc, use_cache):
        """Try to fetch a cached OCR scan for the document. 
        Returns a file-like object if successful.
        """
        if not use_cache:
            return None

        # Cache key is formed from thumbprint of document.  We don't
        # care how that is determined but assume it's cryptographically
        # secure. 
        s3 = self._get_cache_s3_connection()
        cache_key = "cached_scan_{0}".format(doc.thumbprint)
        if s3.exists(cache_key):
            # Stream data into 
            logger.info(
                "Found cached OCR for doc: key={0}, ref={1}".format(cache_key,
                                                                    doc.locator))
            temp_ocr_file = tempfile.NamedTemporaryFile(delete=False)
            cached_ocr_stream = s3.fetch_to_stream(cache_key)
            temp_ocr_file.write(cached_ocr_stream.read())
            temp_ocr_file.flush()
            temp_ocr_file.seek(0)
            return temp_ocr_file
        else:
            logger.debug(
                "No cached OCR for doc: key={0}, ref={1}".format(cache_key,
                                                                 doc.locator))
            return None

    def _load_to_cache(self, path, thumbprint, locator):
        """Stores OCR translation in S3 using thumbprint of doc content as key"""
        s3 = self._get_cache_s3_connection()
        cache_key = "cached_scan_{0}".format(thumbprint)

        # Delete existing copy, if any.
        if s3.exists(cache_key):
            logger.info("Clearing OCR cache result: key={0}, locator={1}".format(cache_key, locator))
            s3.delete(cache_key)

        metadata = {"locator": locator}
        logger.info("Uploading OCR cache result: key={0}, locator={1}, local_path={2}".format(
            cache_key, locator, path))
        s3.create_from_file(cache_key, path, metadata)

    def _fetch_from_s3(self, locator, thumbprint):
        """Fetch content corresponding to an S3 locator. 
        """
        # Connect and confirm the document exists.
        s3_ref = S3Ref(locator)
        s3 = self._get_document_s3_connection(s3_ref.bucket, s3_ref.region)
        if not s3.exists(s3_ref.key):
            raise Exception("Document not found: {0}".format(locator))

        # Download the document.
        temp_doc_file = tempfile.NamedTemporaryFile(delete=False)
        logger.info(
            "Fetching document for OCR: ref={0}, temp_file={1}".format(locator,
                                                                       temp_doc_file.name))
        cached_doc_stream = s3.fetch_to_stream(s3_ref.key)
        temp_doc_file.write(cached_doc_stream.read())
        temp_doc_file.flush()
        temp_doc_file.seek(0)

        # Confirm the thumbprint
        sha256_sum = sha256(temp_doc_file.name)
        if sha256_sum == thumbprint:
            logger.info(
                "Fetched document thumbprint matches expected value: temp_file={0}, thumbprint={1}"
                .format(temp_doc_file.name, thumbprint))
        else:
            raise Exception(
                "Fetched document thumbprint does not match value: ref={0}, temp_file={1}, expected={2}, actual={3}"
                .format(locator, temp_doc_file.name, thumbprint, sha256_sum))

        # Adjust file pointer and return.
        temp_doc_file.seek(0)
        return temp_doc_file

    def _run_ocr(self, file):
        """Dispatches file-like object to OCR, returns XML file path if successful"""
        ocr_engine = CloudOCR(application_id=self._ocr_config['abbyy']['appid'],
                                   password=self._ocr_config['abbyy']['pwd'])
        scan_file = {file.name: file}
        result = ocr_engine.process_and_download(scan_file,
                                                      exportFormat='xml',
                                                      language='English')

        logger.info("Output result: {0}".format(result))
        logger.info("Output bytes: {0}".format(result.get("xml")))
        result_bytes = result.get("xml")
        result_path = "/tmp/scan.dat"
        with open(result_path, mode="wb") as output:
            output.write(result_bytes.read())
            output.flush()
            output.close()
        return result_path

    def _get_cache_s3_connection(self):
        """Allocate connection to S3 from configuration file"""
        access_key_id = self._ocr_config['aws']['accessKeyId']
        secret_access_key = self._ocr_config['aws']['secretAccessKey']
        bucket = self._ocr_config['cache']['bucket']
        location = self._ocr_config['cache']['location']

        return S3Connection(access_key_id=access_key_id,
                            secret_access_key=secret_access_key,
                            bucket=bucket,
                            location=location,
                            create_bucket=True)

    def _get_document_s3_connection(self, bucket, region):
        """Allocate connection to an arbitrary bucket"""
        access_key_id = self._ocr_config['aws']['accessKeyId']
        secret_access_key = self._ocr_config['aws']['secretAccessKey']

        s3_conn = S3Connection(access_key_id=access_key_id,
                               secret_access_key=secret_access_key,
                               bucket=bucket,
                               location=region,
                               create_bucket=False)
        s3_conn.validate()
        return s3_conn

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

    def _make_ocr_path(self, sha256):
        """Computes cache path for a scanned file using sha256 hash of 
        said file
        """
        ocr_path = self._ocr_cache_dir + "/" + sha256
        return ocr_path
