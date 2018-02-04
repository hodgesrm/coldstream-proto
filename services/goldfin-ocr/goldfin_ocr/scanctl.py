#!/usr/bin/env python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Invoice processing command line interface (CLI)"""

# Standard Python libraries. 
import argparse
import json
import logging
import os

from api.models import Document
#from api.util import deserialize_model
from json_xlate import json_dict_to_model, model_to_json_dict

# Standard logging initialization.
logger = logging.getLogger(__name__)


#############################################################################
# Utility functions
#############################################################################

def dump_swagger_object_to_json(obj, indent=None, sort_keys=None):
    """Dumps a generated swagger object to JSON by supplying default to
    convert objects to dictionaries. 
    a dictionary"""
    converter_fn = lambda unserializable_obj: unserializable_obj.to_dict()
    return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)


def init_logging(log_level, log_file=None):
    """Validates log level and starts logging"""
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


#############################################################################
# Command line processor
#############################################################################

# Define top-level command line parsing.
parser = argparse.ArgumentParser(prog='scanctl.py',
                                 usage="%(prog)s [options]")
parser.add_argument("--daemon",
                    help="Process OCR operations from queue", 
                    action="store_true", default=False)
parser.add_argument("--request", 
                    help="Process OCR single request",
                    type=str, choices=['scan', 'validate'])
parser.add_argument("--body", help="OCR Request body")
parser.add_argument("--aws-cfg",
                    help="Cloud services configuration file",
                    default="conf/aws.yaml")
parser.add_argument("--ocr-cfg",
                    help="OCR configuration file", 
                    default="conf/ocr.yaml")
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", None))

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Start logging.
init_logging(log_level=args.log_level, log_file=args.log_file)

# Process options. 
if args.daemon is True:
    raise Exception("Daemon operation is not supported yet")
else:
    request = args.request
    if args.body is None:
        raise Exception("Request body is required")
    with open(args.body, "r") as fp:
        body = json.load(fp)

    print("Request: {0}".format(request))
    print("Body: {0}".format(body))
    print("Body: {0}".format(type(body)))

    document = json_dict_to_model(body, Document)
    print("Document: {0}".format(document))
    back_to_json = model_to_json_dict(document)
    print("JSON: {0}".format(back_to_json))

print("Ready do to something")
