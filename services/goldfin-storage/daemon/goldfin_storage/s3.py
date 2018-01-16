# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

import base64
import boto3
import hashlib
import io
import json
import os
import tempfile
import shutil
import uuid
import goldfin_storage.config as config
from .models.document import Document
from .models.tenant_info import TenantInfo

# Our logger.
import logging

logger = logging.getLogger(__name__)


def initializeFromEnvironmental(name="GOLDFIN_STORAGE_CONF"):
    """Initialize server from config name stored in environmental variable
        :param name: Environmental variable name
        :type: str

        :rtype S3BucketServer
    """
    logger.info("Fetching config file name from environment: {0}".format(name))
    config_file_name = os.environ[name]
    return initializeFromFile(config_file_name)


def initializeFromFile(config_file_name):
    """Initialize server instance from file.
        :param config_file_name: Configuration file name
        :type: str

        :rtype S3BucketServer
    """
    logger.info("Loading config from file: {0}".format(config_file_name))
    config_data = config.load(config_file_name)
    print(config_data)
    s3server = S3BucketServer(config_data)
    s3server.init()
    return s3server


class S3BucketServer(object):
    """Implements a server that manages tenant files in a single S3 bucket."""

    def __init__(self, s3config):
        self._access_key_id = s3config.get("aws", "access_key_id")
        self._secret_access_key = s3config.get("aws", "secret_access_key")
        self._bucket = s3config.get("s3", "bucket")
        self._location = s3config.get("s3", "location")

    def init(self):
        """Ensure the storage bucket exists"""
        s3 = boto3.resource('s3', aws_access_key_id=self._access_key_id,
                            aws_secret_access_key=self._secret_access_key)
        logger.info("Initializing storage: bucket={0}, location={1}".format(
            self._bucket, self._location))
        bucket = s3.Bucket(self._bucket)
        if bucket is None:
            logger.info("Creating bucket: {0}".format(self._bucket))
            s3.create_bucket(Bucket=self._bucket, CreateBucketConfiguration={
                'LocationConstraint': self._location})
            bucket = s3.Bucket(self._bucket)
        logger.info("Initialization complete")

    def tenant_create(self, tenant):
        """
        Create object to represent tenant metadata.
        :param tenant: tenant information
        :type tenant: TenantInfo

        :rtype TenantInfo
        """
        logger.info("Creating tenant: tenant_id={0}, name={1}".format(
            tenant['tenantId'], tenant['name']))
        bucket = self._getBucket()
        key = "tenant/{0}".format(tenant['tenantId'])
        body = str(tenant)
        bucket.put_object(Body=body, Key=key)
        logger.info("Stored tenant: key={0}".format(key))
        return tenant

    def tenant_delete(self, tenantId):
        """
        Delete tenant
        :param tenantId: Tenant ID
        :type tenantId: str
        :return: None
        """
        logger.info("Deleting tenant: tenant_id={0}".format(tenantId))
        bucket = self._getBucket()
        key = self._tenantKey(tenantId)
        bucket.delete_objects(Delete={'Objects': [{'Key': key}]})

    def tenant_show(self, tenantId):
        """
        Show tenant metadata
        :param tenantId: Tenant ID
        :type tenantId: str
        :return: None
        """
        key = self._tenantKey(tenantId)
        obj = self._getObject(key)
        return obj.get()['Body'].read().decode('utf-8')

    def tenant_document_create(self, tenantId, stream, name, description,
                               content_length, content_type):
        """
        Create a new document for tenant
        Upload a new document including metadata
        :param tenantId: Tenant ID
        :type tenantId: str
        :param file: Document file
        :type file: stream
        :param name: Document file name
        :type name: str
        :param description: Description of the document
        :type description: str

        :rtype: Document
        """
        # Download the file to a temporary location so we can check the length.
        with tempfile.NamedTemporaryFile(delete=False) as mytemp:
            # Copy data into tempfile so we can compute a hash on it and
            # get the content length.
            tempname = mytemp.name;
            shutil.copyfileobj(stream, mytemp)
            mytemp.flush()
            mytemp.seek(0)
            filesize = os.path.getsize(tempname)
            sha256 = self._sha256(tempname)
            logger.info("tempname: {0}, sha256: {1}".format(tempname, sha256))

            # Compute the document.
            doc = Document()
            doc.id = str(uuid.uuid4())
            doc.name = str(name)
            doc.locator = str(self._objectKey(tenantId, doc.id))
            doc.thumbprint = sha256
            doc.creation_date = ""
            #print("doc: " + str(doc))
            #print("doc.to_dict(): " + str(doc.to_dict()))
            logger.info("json.dumps(doc.to_dict()): " + json.dumps(doc.to_dict()))

            logger.info(
                "Storing document: tenant_id={0}, locator={1}, name={2}, content_type={3}, content_length={4}".format(
                    tenantId, doc.locator, doc.name, content_type, filesize))
            s3_object = self._getObject(doc.locator)
            s3_object.put(Body=mytemp, Metadata=dict(
               tenant_id=tenantId, name=name, description=description, sha_256=sha256
               ), ContentType=content_type, ContentLength=filesize)
            #            ), ContentType=content_type, ContentLength=filesize)

            os.unlink(tempname)

            # Return dict to prevent serialization error.
            return doc.to_dict()

    def tenant_document_content(self, tenantId, docId):
        """
        Download document content
        Return binary data
        :param tenantId: Tenant ID
        :type tenantId: str
        :param docId: Document ID
        :type docId: str

        :rtype: stream
        """
        key = self._objectKey(tenantId, docId)
        logger.info("Fetching document: key={0}".format(key))
        s3_object = self._getObject(key)
        print(s3_object)
        response = s3_object.get()
        #buffer = io.BytesIO(response['Body'].read())
        #return buffer
        return response['Body']

    def _tenantKey(self, tenantId):
        """Generate storage key for a tenant"""
        return "tenant/{0}".format(tenantId)

    def _objectKey(self, tenantId, objectId):
        """Generate storage key for a tenant file"""
        return "tenant/{0}/{1}".format(tenantId, objectId)

    def _getBucket(self):
        """Returns a specific bucket resource"""
        s3 = boto3.resource('s3', aws_access_key_id=self._access_key_id,
                            aws_secret_access_key=self._secret_access_key)
        bucket = s3.Bucket(self._bucket)
        return bucket

    def _getObject(self, key):
        s3 = boto3.resource('s3', aws_access_key_id=self._access_key_id,
                            aws_secret_access_key=self._secret_access_key)
        obj = s3.Object(self._bucket, key)
        return obj

    def _sha256(self, path):
        """Computes SHA-256 hash on file"""
        sha256 = hashlib.sha256()
        with open(path, 'rb') as f:
            for block in iter(lambda: f.read(1024), b''):
                sha256.update(block)
        return sha256.hexdigest()