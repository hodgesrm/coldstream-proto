# Copyright (c) 2018 Goldfin Systems LLC. All rights reserved. 

"""Implementation for data series processing"""
import json
import logging
import os
import shutil

from .util import sha256
from .s3 import S3Connection, S3Ref
from .data.pfactory import get_provider
from .api.models.observation import Observation
from .api.models.data_series import DataSeries
from .json_xlate import json_dict_to_model, SwaggerJsonEncoder

# Standard logging initialization.
logger = logging.getLogger(__name__)


class DataProcessor:
    """Translates data series records to inventory data"""

    def __init__(self, data_config):
        """Set up service including creating work directory

        :param data_config: Data service configuration parameters
        :type data_config: dict
        """
        # Save the configuration.
        self._data_config = data_config

        # Ensure work directory exists.
        self._data_work_dir = self._data_config['data']['work_dir']
        logger.info(
            "Initializing work directory: {0}".format(self._data_work_dir))
        os.makedirs(self._data_work_dir, exist_ok=True)
        if not os.path.exists(self._data_work_dir):
            raise AssertionError(
                "Cannot create work directory: {0}".format(self._data_work_dir))

    def process(self, tenant_id, data_series, preserve_work_files=False):
        """Process a data series and return inventory data. 

        :param tenant_id: Tenant to whom data belongs
        :type tenant_id: str
        :param data_series: Definition of data series to be scanned
        :type data_series: DataSeries
        :param preserve_work_files: If true keep all work files, otherwise delete on success
        :type preserve_work_files: bool
        :returns: Inventory object or None
        """
        logger.info("Processing data series: tenant_id={0}, data_series={1}"
                    .format(tenant_id, data_series))

        # We only support application/json for now. 
        if data_series.content_type.rfind("application/json") >= 0:
            logger.debug("Content type is supported: {0}".format(
                         data_series.content_type))
        else:
            raise Exception(
                      "Unsupported data series content type: {0}".format(
                           data_series.content_type))

        # Create a work directory.  This will hold all files and will
        # be cleared on success (if desired).  Start by recording the data
        # series request there.
        ds_work_dir = self._create_ds_work_dir(data_series.id)
        with open(os.path.join(ds_work_dir, 'data_series.json'),
                  "w") as ds_file:
            ds_file.write(self._dump_to_json(data_series, external=True))

        # Download observation data from S3.
        logger.info("Fetching data series file: ref={0}".format(data_series.locator))
        ds_input_path = self._fetch_from_s3(data_series.locator, 
                                            data_series.thumbprint, 
                                            ds_work_dir, data_series.name)
        logger.info("Path location of data series data: {0}".format(ds_input_path))

        with open(ds_input_path, "r") as ds_file:
            ds = ds_file.read()
        observation = json_dict_to_model(json.loads(ds), Observation)
        observation_path = ds_input_path + "-observation.json"
        logger.info(
            "Storing observation to file: {0}".format( observation_path))
        with open(observation_path, "w") as observation_file:
            observation_file.write(self._dump_to_json(observation))

        # Get a provider.
        provider = get_provider(observation)
        if provider is None:
            message = "Can't find a provider!!! data_series={0}, vendor={2}".format(
                    data_series.id, observation.vendor_identifier)
            logger.error(message)
            logger.info("Preserving work files: {0}".format(ds_work_dir))
            print(message)
            raise Exception("Unable to find provider for data series content")
        else:
            # Remove work files on success unless client wants to keep.
            results = provider.get_results(observation, data_series.id)
            print(type(results))
            if preserve_work_files:
                # Dump the invoice to file.
                results_path = os.path.join(ds_work_dir, 'results.json')
                logger.info(
                    "Storing results to file: {0}".format(
                        results_path))
                with open(results_path, "w") as results_file:
                    results_file.write(self._dump_to_json(results, external=True))
                logger.info("Preserving doc files: {0}".format(ds_work_dir))
            else:
                logger.info("Removing doc work files on success: {0}".format(ds_work_dir))
                shutil.rmtree(ds_work_dir, ignore_errors=True)
                if os.path.exists(ds_work_dir):
                    logger.warning("Unable to delete doc work files!")

            return results

    def _create_ds_work_dir(self, ds_id):
        """Create a work directory for data series processing

        :param ds_id: Unique data series ID
        :type ds_id: str
        :return: str
        """
        ds_work_dir_base = os.path.join(self._data_work_dir, ds_id)
        dir_suffix = 0
        while True:
            dir_suffix += 1
            ds_work_dir = "{base}_{suffix}".format(
                base=ds_work_dir_base, suffix=dir_suffix)
            if not os.path.exists(ds_work_dir):
                logger.info("Creating data series work directory: {0}".format(
                    ds_work_dir))
                os.mkdir(ds_work_dir)
                return ds_work_dir

    def _fetch_from_s3(self, locator, thumbprint, ds_work_dir, ds_name):
        """Fetch content corresponding to an S3 locator.
        """
        # Connect and confirm the content exists.
        s3_ref = S3Ref(locator)
        s3 = self._get_content_s3_connection(s3_ref.bucket, s3_ref.region)
        if not s3.exists(s3_ref.key):
            raise Exception("Data series not found: {0}".format(locator))

        # Download the content..
        temp_ds_path = os.path.join(ds_work_dir, ds_name)
        logger.info(
            "Fetching data series for analysis: ref={0}, file={1}".format(locator, temp_ds_path))
        with open(temp_ds_path, "wb") as temp_ds_file:
            cached_ds_stream = s3.fetch_to_stream(s3_ref.key)
            temp_ds_file.write(cached_ds_stream.read())
            temp_ds_file.flush()

        # Confirm the thumbprint
        sha256_sum = sha256(temp_ds_path)
        if sha256_sum == thumbprint:
            logger.info(
                "Fetched content thumbprint matches expected value: file={0}, thumbprint={1}"
                    .format(temp_ds_path, thumbprint))
        else:
            raise Exception(
                "Fetched content thumbprint does not match value: ref={0}, file={1}, expected={2}, actual={3}"
                    .format(locator, temp_ds_path, thumbprint,
                            sha256_sum))

        # Return the data series path value.
        return temp_ds_path

    def _get_content_s3_connection(self, bucket, region):
        """Allocate connection to an arbitrary bucket"""
        access_key_id = self._data_config['aws']['accessKeyId']
        secret_access_key = self._data_config['aws']['secretAccessKey']

        s3_conn = S3Connection(access_key_id=access_key_id,
                               secret_access_key=secret_access_key,
                               bucket=bucket,
                               location=region,
                               create_bucket=False)
        s3_conn.validate()
        return s3_conn

    def _dump_to_json(self, obj, indent=2, sort_keys=True, external=False):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        if external:
            encoder = SwaggerJsonEncoder()
            return encoder.encode(obj)
        else:
            converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
            return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)
