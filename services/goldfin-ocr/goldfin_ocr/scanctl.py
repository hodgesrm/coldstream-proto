#!/usr/bin/env python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Invoice processing command line interface (CLI)"""

# Standard Python libraries. 
import argparse
import json
import logging
import os
import time
import yaml

# This is the top-level script, so relative imports not allowed.
from goldfin_ocr.api.models import Document, ApiResponse
from goldfin_ocr.json_xlate import json_dict_to_model, model_to_json_dict, \
    SwaggerJsonEncoder
from goldfin_ocr.ocr import OcrProcessor
import goldfin_ocr.util as util
import goldfin_ocr.sqs as sqs

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


def process_from_command_line(args, ocr_config):
    """Process a single request using command line arguments

    :param args: Argparse arguments
    :param ocr_config: OCR config file contents
    """
    request = args.request
    if args.body is None:
        raise Exception("Request body is required")
    with open(args.body, "r") as fp:
        body = json.load(fp)

    # Normalize the JSON and convert to document.
    logger.debug("Request: {0}".format(request))
    document = json_dict_to_model(body, Document)

    # Invoke OCR scan.
    ocr_processor = OcrProcessor(ocr_config)
    invoice = ocr_processor.scan("ignore", document,
                                 use_cache=(not args.no_cache),
                                 preserve_work_files=args.preserve_work_files)
    if invoice is None:
        print("No invoice generated!")
    else:
        print("Invoice: {0}".format(invoice))
        encoder = SwaggerJsonEncoder()
        back_to_json = encoder.encode(invoice)
        # back_to_json = model_to_json_dict(invoice)
        print("JSON: {0}".format(back_to_json))


def process_from_queue(args, ocr_config):
    """Process requests read from a queue.

    :param args: Argparse arguments
    :param ocr_config: OCR config file contents
    """
    # Get queue connectors.
    request_queue = get_sqs_connection('ocrRequestQueue', ocr_config)
    response_queue = get_sqs_connection('ocrResponseQueue', ocr_config)
    ocr_processor = OcrProcessor(ocr_config)

    # Loop until iteration count is exhausted.
    count = 0
    while args.iterations == -1 or count < args.iterations:
        # Wait for a message.
        request = None
        while request is None:
            request = request_queue.receive()
            if request is None:
                time.sleep(1)
            else:
                count += 1
                request_queue.delete(request)

        # Normalize the JSON content and convert to Document type.
        logger.info("Document OCR request: {0}".format(request))
        content = json.loads(request.content)
        document = json_dict_to_model(content, Document)

        # Invoke OCR scan.
        try:
            invoice = ocr_processor.scan(request.tenant_id, document,
                                         use_cache=(not args.no_cache),
                                         preserve_work_files=args.preserve_work_files)
            if invoice is None:
                # Add error handling here.
                logger.error("Unable to generate invoice")
                dump_document_to_file(document.id, request.content)
                response_content = ApiResponse(code=500, type="error",
                                               message="Unable to scan invoice")
                response_content_class = "ApiResponse"

            else:
                logger.debug("Invoice: {0}".format(invoice))
                encoder = SwaggerJsonEncoder()
                response_content = encoder.encode(invoice)
                response_content_class = "Invoice"

        except Exception as err:
            # Add error handling here.
            msg = "Unable to scan invoice: reason={0}".format(err)
            logger.error(msg, err)
            dump_document_to_file(document.id, request.content)
            encoder = SwaggerJsonEncoder()
            response_content = encoder.encode(
                ApiResponse(code=500, type="error",
                            message=msg))
            response_content_class = "ApiResponse"

        # Send response back on response queue.
        response = sqs.StructuredMessage()
        response.operation = request.operation
        response.type = "response"
        response.xact_tag = request.xact_tag
        response.tenant_id = request.tenant_id
        response.content = response_content
        response.content_class = response_content_class
        response_queue.send(response)

def dump_document_to_file(id, content):
    document_path = "/tmp/document-" + id + ".json"
    logger.info(
        "Storing document request to file: {0}".format(
            document_path))
    with open(document_path, "w") as document_file:
        document_file.write(content)
    return document_path


def get_sqs_connection(queue_opt, config):
    """Allocate connection from configuration file"""
    access_key_id = config['aws']['accessKeyId']
    secret_access_key = config['aws']['secretAccessKey']
    queue = config['sqs'][queue_opt]
    region = config['sqs']['region']

    queue_conn = sqs.SqsConnection(queue, access_key_id=access_key_id,
                                   secret_access_key=secret_access_key,
                                   region=region)
    if not queue_conn.queueExists():
        raise Exception("Queue does not exist: {0}".format(queue))
    else:
        return queue_conn


#############################################################################
# Command line processor
#############################################################################

# Define top-level command line parsing.
parser = argparse.ArgumentParser(prog='scanctl.py',
                                 usage="%(prog)s [options]")
parser.add_argument("--daemon",
                    help="Process OCR operations from queue",
                    action="store_true", default=False)
parser.add_argument("--iterations",
                    help="Number of messages to process on queue (-1 = loop forever)",
                    type=int, default=-1)
parser.add_argument("--request",
                    help="Process OCR single request",
                    type=str, choices=['scan', 'validate'])
parser.add_argument("--body", help="OCR Request body")
parser.add_argument("--no-cache",
                    help="Do not use cache for scanning results (expensive!)",
                    action="store_true", default=False)
parser.add_argument("--preserve-work-files",
                    help="Keep all work files even if scan is successful",
                    action="store_true", default=False)
parser.add_argument("--ocr-cfg",
                    help="OCR configuration file",
                    default="conf/ocr.yaml")
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", "ocr.log"))

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Start logging.
init_logging(log_level=args.log_level, log_file=args.log_file)

# Load the ocr configuration.
with open(args.ocr_cfg, "r") as ocr_yaml:
    ocr_config = yaml.load(ocr_yaml)

# Fork processing depending on whether we are a daemon or a command line request.
if args.daemon is True:
    process_from_queue(args, ocr_config)
else:
    process_from_command_line(args, ocr_config)

print("Done!!!")