# Copyright (c) 2018 Goldfin Systems LLC.  All rights reserved. 

"""Public API for invoice processing"""

import gzip
import hashlib
import io
import json
import logging
import os.path
import yaml

logger = logging.getLogger(__name__)


def sha256(path):
    """Compute SHA-256 hash on file

    :param path: Path to file
    :type path: str
    :returns str
    """
    sha256 = hashlib.sha256()
    with open(path, 'rb') as f:
        for block in iter(lambda: f.read(1024), b''):
            sha256.update(block)
    return sha256.hexdigest()


def dump_to_json(obj, indent=2, sort_keys=True):
    """Dumps a object to JSON by supplying default to
    convert objects to dictionaries.

    :param obj: Object to serialized to JSON
    :type obj: Object
    :param indent: Indentation spaces
    :type indent: int
    :param sort_keys: Sort keys in order
    :type sort_keys: bool
    """
    converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
    return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)

def string_to_gzipped_utf8(string_value):
    """Serialize string value to gzip'ed UTF8 bytes"""
    out = io.BytesIO()
    with gzip.GzipFile(fileobj=out, mode='w') as fo:
        fo.write(string_value.encode())
    bytes_obj = out.getvalue()
    return bytes_obj

def gzipped_utf8_to_string(byte_array):
    """Deserialize gzip'ed UTF8 bytes to a Python string"""
    bytes_obj = io.BytesIO()
    bytes_obj.write(byte_array)
    bytes_obj.seek(0)
    with gzip.GzipFile(fileobj=bytes_obj, mode='r') as fo:
        unzipped_bytes = fo.read()
    return unzipped_bytes.decode()

def init_logging(log_level, log_file=None):
    """Validates log level and starts logging

    :param log_level: Log level string label
    :type log_level: str
    :param log_file: Log file name (defaults to console)
    :type log_file: str
    """
    if log_level == "CRITICAL":
        level = logging.CRITICAL
    elif log_level == "ERROR":
        level = logging.ERROR
    elif log_level == "WARNING":
        level = logging.WARNING
    elif log_level == "INFO":
        level = logging.INFO
    elif log_level == "DEBUG":
        level = logging.DEBUG
    else:
        raise Exception("Unknown log level: " + log_level)

    if log_file is None:
        logging.basicConfig(level=level)
    else:
        logging.basicConfig(filename=log_file, level=level)

    logger.debug(
        "Initializing log: log_file={0}, log_level={1}".format(log_file,
                                                               log_level))

def get_required_config(config_file_name):
    """Locates and loads a required YAML configuration file into a dictionary.

    Observes the standard config file path.  First look in 
    $GOLDFIN_CONFIG_DIR, next /var/lib/goldfin/conf, then finally in 
    $PWD/conf.  Raises an exception if we cannot successfully load using
    this path.

    :param config_file_name: Name of the file
    :type config_file_name: string
    """
    paths = []
    goldfin_config_dir = os.getenv('GOLDFIN_CONFIG_DIR')
    if goldfin_config_dir:
        paths.append(goldfin_config_dir)
    paths.append('/var/lib/goldfin/conf')
    paths.append('os.getenv("PWD")' + '/conf')

    for path in paths:
        candidate = os.path.join(path, config_file_name)
        if os.path.exists(candidate):
            logger.info("Loading configuration file: {0}".format(candidate))
            with open(candidate, "r") as config_file:
                config = yaml.load(config_file)
                return config

    # Config files are required.  No point in continuing.
    raise Exception("Unable to find config file: {0}".format(config_file_name))
