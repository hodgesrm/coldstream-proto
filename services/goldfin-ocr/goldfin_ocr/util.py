# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import hashlib
import json
import logging

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
