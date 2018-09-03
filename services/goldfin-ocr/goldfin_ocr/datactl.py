#!/usr/bin/env python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Processor for managing data requests"""

# Standard Python libraries. 
import argparse
import json
import logging
import os
import time
import yaml

# This is the top-level script, so relative imports not allowed.
from goldfin_ocr.api.models import DataSeries, Host, ApiResponse
from goldfin_ocr.json_xlate import json_dict_to_model, model_to_json_dict, \
    SwaggerJsonEncoder
from goldfin_ocr.data_series_processor import DataProcessor
import goldfin_ocr.util as util
import goldfin_ocr.sqs as sqs

# Standard logging initialization.
logger = logging.getLogger(__name__)


#############################################################################
# Processor functions.
#############################################################################

def process_from_command_line(args, service_config):
    """Process a single request using command line arguments

    :param args: Argparse arguments
    :param service_config: data series config file contents
    """
    request = args.request
    if args.body is None:
        raise Exception("Data series request body is required")
    with open(args.body, "r") as fp:
        body = json.load(fp)

    # Normalize the JSON and convert to document.
    logger.debug("Request: {0}".format(request))
    data_series = json_dict_to_model(body, DataSeries)

    # Invoke data series analysis.
    ds_processor = DataProcessor(service_config)
    results = ds_processor.process("ignore", data_series,
                                 preserve_work_files=args.preserve_work_files)
    if results is None:
        print("No observation result generated!")
    else:
        print("Results: {0}".format(results))
        encoder = SwaggerJsonEncoder()
        back_to_json = encoder.encode(results)
        # back_to_json = model_to_json_dict(invoice)
        print("JSON: {0}".format(back_to_json))


def process_from_queue(args, service_config):
    """Process requests read from a queue.

    :param args: Argparse arguments
    :param service_config: data series config file contents
    """
    # Get queue connectors.
    request_queue = get_sqs_connection('requestQueue', service_config)
    response_queue = get_sqs_connection('responseQueue', service_config)
    ds_processor = DataProcessor(service_config)

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

        # Normalize the JSON content and convert to DataSeries type.
        logger.info("Data series request: {0}".format(request))
        content = json.loads(request.content)
        data_series = json_dict_to_model(content, DataSeries)

        # Invoke data series analysis.
        try:
            ds_response = ds_processor.process(
                                         request.tenant_id, data_series,
                                         preserve_work_files=args.preserve_work_files)
            if ds_response is None:
                # Add error handling here.
                msg = "No response from series processing"
                logger.error(msg)
                dump_document_to_file(data_series.id, request.content)
                encoder = SwaggerJsonEncoder()
                response_content = encoder.encode(
                                       ApiResponse(code=500, 
                                       type="error",
                                       message=msg))
                response_content_class = "ApiResponse"

            else:
                logger.debug("Data series response: {0}".format(ds_response))
                encoder = SwaggerJsonEncoder()
                response_content = encoder.encode(ds_response)
                response_content_class = "Result"

        except Exception as err:
            # Add error handling here.
            msg = "Unable to process data series: reason={0}".format(err)
            logger.error(msg, err)
            dump_document_to_file(data_series.id, request.content)
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
    group = config['aws']['group']
    access_key_id = config['aws']['accessKeyId']
    secret_access_key = config['aws']['secretAccessKey']
    region = config['aws']['region']
    queue = config['dataSeries'][queue_opt]

    queue_conn = sqs.SqsConnection(queue, group=group,
                                   access_key_id=access_key_id,
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
                    help="Process data series operations from queue",
                    action="store_true", default=False)
parser.add_argument("--iterations",
                    help="Number of messages to process on queue (-1 = loop forever)",
                    type=int, default=-1)
parser.add_argument("--request",
                    help="Process data series single request",
                    type=str, choices=['process'], default='process')
parser.add_argument("--body", help="Data series request body")
parser.add_argument("--preserve-work-files",
                    help="Keep all work files even if processing is successful",
                    action="store_true", default=False)
parser.add_argument("--service-cfg",
                    help="Service configuration file (default: %(default)s)",
                    default="service.yaml")
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", "data.log"))

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Start logging.
util.init_logging(log_level=args.log_level, log_file=args.log_file)

# Load the service configuration.
service_config = util.get_required_config(args.service_cfg)

# Fork processing depending on whether we are a daemon or a command line request.
if args.daemon is True:
    process_from_queue(args, service_config)
else:
    process_from_command_line(args, service_config)

print("Done!!!")
