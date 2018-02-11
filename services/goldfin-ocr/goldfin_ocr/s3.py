# Copyright (c) 2017-2018 Robert Hodges.  All rights reserved. 

import boto3
import botocore
import hashlib
import urllib

# Our logger.
import logging

logger = logging.getLogger(__name__)


class S3Ref(object):
    """Parses components of an S3 reference."""
    def __init__(self, locator):
        """Initialize connection

        :param locator: S3 URL
        :type locator: str
        """
        parsed_url = urllib.parse.urlparse(locator)
        host_components = parsed_url.netloc.split('.')
        if len(host_components) == 5 and host_components[1] == "s3":
            self.bucket = host_components[0]
            self.region = host_components[2]
            self.key = parsed_url.path[1:]
        else:
            raise Exception("Unrecognized S3 locator: {0}".format(locator))


class S3Connection(object):
    """Implements a server that manages tenant files in a single S3 bucket.
    """

    def __init__(self, access_key_id=None, secret_access_key=None,
                 bucket=None, location=None, create_bucket=False):
        """Initialize connection

        :param access_key_id: AWS access key
        :type access_key_id: str
        :param secret_access_key: AWS secret key
        :type secret_access_key: str
        :param bucket: S3 bucket name
        :type bucket: str
        :param location: S3 storage location
        :type location: str
        """
        self._access_key_id = access_key_id
        self._secret_access_key = secret_access_key
        self._bucket = bucket
        self._location = location
        self._create_bucket = create_bucket

        self._s3 = boto3.resource('s3',
                                  aws_access_key_id=self._access_key_id,
                                  aws_secret_access_key=self._secret_access_key,
                                  region_name=self._location)

    # Experimental support for Python context management (i.e., 'with'
    # syntax.
    def __enter__(self):
        """Return self for context management"""
        logger.debug("Entered S3 Connection")
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Try to close boto connection"""
        try:
            self._s3.meta.client._endpoint.http_session.close()
            logger.debug("Exited S3 connection")
        except Exception:
            logger.warning("Unable to close boto endpoint")

    def validate(self):
        """Ensure the storage bucket exists and optionally create if not"""
        logger.info("Initializing storage: bucket={0}, location={1}".format(
            self._bucket, self._location))
        exists = True
        try:
            self._s3.meta.client.head_bucket(Bucket=self._bucket)
        except botocore.exceptions.ClientError as e:
            # If a client error is thrown, then check that it was a 404 error.
            # If it was a 404 error, then the bucket does not exist.
            error_code = int(e.response['Error']['Code'])
            if error_code == 404:
                exists = False

        if exists is False:
            if self._create_bucket:
                logger.info("Creating bucket: {0}".format(self._bucket))
                self._s3.create_bucket(Bucket=self._bucket,
                                       CreateBucketConfiguration={
                                           'LocationConstraint': self._location})
            else:
                raise Exception("Bucket does not exist: {0}".format(
                    self._bucket))
        logger.info("Initialization complete")

    def create(self, key, body, metadata=None):
        """Create document from string

        :param key: Object key
        :type key: str
        :param body: Object string
        :type body: str
        :param metadata: Object metadata
        :type metadata: dict
        """
        bucket = self._get_bucket()
        if metadata:
            bucket.put_object(Body=body, Key=key, Metadata=metadata)
            logger.info("Stored object: key={0} metadata={1}".format(key,
                                                                     metadata))
        else:
            bucket.put_object(Body=body, Key=key)
            logger.info("Stored object: key={0}".format(key))

    def create_from_file(self, key, path, metadata=None):
        """Create a new document from file data

        :param key: Object key
        :type key: str
        :param path: File name
        :type path: str
        :param metadata: Object metadata
        :type metadata: dict
        """
        with open(path, "rb") as content:
            logger.info(
                "Storing object: key={0}, path={1}".format(key, path))
            s3_object = self._get_object(key)

            if metadata:
                s3_object.upload_fileobj(content,
                                         ExtraArgs={"Metadata": metadata})
                logger.info("Storing object: key={0} metadata={1}".format(
                    key, metadata))
            else:
                s3_object.upload_fileobj(content)
                logger.info("Storing object: key={0}".format(key))

    def fetch(self, key):
        """Fetch document.

        :param key: Object key
        :type key: str
        :return: str
        """
        obj = self._get_object(key)
        return obj.get()['Body'].read().decode('utf-8')

    def fetch_metadata(self, key):
        """Fetch document metadata

        :param key: Object key
        :type key: str
        :return: dict
        """
        obj = self._get_object(key)
        return obj.get()['Metadata']

    def fetch_to_stream(self, key):
        """
        :param key: Object key
        :type key: str
        :rtype: stream
        """
        logger.info("Fetching document: key={0}".format(key))
        s3_object = self._get_object(key)
        print(s3_object)
        response = s3_object.get()
        # buffer = io.BytesIO(response['Body'].read())
        # return buffer
        return response['Body']

    def exists(self, key):
        """Return True if object exists

        :param key: Object key
        :type key: str
        :return: bool
        """
        try:
            obj = self._get_object(key)
            obj.get()
            return True
        except botocore.exceptions.ClientError as e:
            if e.response['Error']['Code'] == 'NoSuchKey':
                return False
            else:
                raise e

    def delete(self, key):
        """Delete document

        :param key: Object key
        :type key: str
        """
        logger.info("Deleting object: key={0}".format(key))
        bucket = self._get_bucket()
        bucket.delete_objects(Delete={'Objects': [{'Key': key}]})

    def _get_bucket(self):
        """Returns a specific bucket resource"""
        bucket = self._s3.Bucket(self._bucket)
        return bucket

    def _get_object(self, key):
        """Return an object resource"""
        obj = self._s3.Object(self._bucket, key)
        return obj

    @staticmethod
    def _sha256(path):
        """Computes SHA-256 hash on file"""
        sha256 = hashlib.sha256()
        with open(path, 'rb') as f:
            for block in iter(lambda: f.read(1024), b''):
                sha256.update(block)
        return sha256.hexdigest()
