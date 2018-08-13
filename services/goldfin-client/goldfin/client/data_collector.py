# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Inventory analysis client"""

import argparse
import configparser
import json
import logging
import os
import sys

from goldfin.client.json_xlate import SwaggerJsonEncoder
from goldfin.client.collectors.ifactory import get_provider

import time
import goldfin.client.api as api
from goldfin.client.api.rest import ApiException
from pprint import pprint

# Standard logging initialization.
logger = logging.getLogger(__name__)

#############################################################################
# Utility functions
#############################################################################

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

"""Print normal informational message."""
def print_info(msg):
    logger.info(msg)
    print(msg)

"""Print error message."""
def print_error(msg):
    logger.error(msg)
    print(msg)

"""Upload data series to API server."""
def do_file_upload(file, goldfin_section):
    # Set client configuration variables. 
    url = 'https://{0}:{1}/api/v1'.format(goldfin_section['api_host'],
                                          goldfin_section['api_port'])
    api.configuration.host = url
    api.configuration.api_key['vnd.io.goldfin.apikey'] = goldfin_section['api_secret_key']
    #api.configuration.verify_ssl = goldfin_section['verify_ssl']
    api.configuration.verify_ssl = False

    try:
        # Upload document
        api_instance = api.InventoryApi()
        description = 'description_example' 
        api_response = api_instance.data_create(file, 
                                                description=description)
        pprint(api_response)
    except ApiException as e:
        print("Exception when calling DocumentApi->document_create: %s\n" % e)

"""Perform a data scan and optional upload resulting data series file."""
def scan(args):
    # Ensure we have a collector type and can find implementation class. 
    if args.type is None:
        print_error("You must specify a collector type with --collector")
        sys.exit(1)

    # Ensure we have a writable output director
    if args.out_dir is None:
        print_error("You must specify an output directory --out-dir")
        sys.exit(1)
    os.makedirs(args.out_dir, exist_ok=True)
    if not os.path.exists(args.out_dir) or not os.access(args.out_dir, os.W_OK):
        print_error("Output directory does not exist or is not writable: {0}".format(args.out_dir))
        return 1

    # Load config for collector.
    if not os.path.exists(args.config):
        print_error("Config file does not exist: {0}".format(args.config))
        return 1

    config_parser = configparser.ConfigParser()
    with open(args.config, "r") as config_file:
        config_parser.read_file(config_file)
    section = config_parser[args.type]
    params = {}
    for key in section:
        params[key] = section[key]
    provider = get_provider(args.type, params)
    obs = provider.execute()
    name = "{0}-{1}.json".format(obs.vendor_identifier, obs.effective_date.strftime("%Y-%m-%d_%H:%M:%S"))
    output_file = os.path.join(args.out_dir, name)
    with open(output_file, "w") as obs_file:
        print_info("Writing observation to output file: {0}".format(output_file))
        encoder = SwaggerJsonEncoder()
        content = encoder.encode(obs)
        obs_file.write(content)

    # Optionally load file to Goldfin.
    if args.upload:
        do_file_upload(output_file, config_parser['goldfin'])

    return 0
    
"""Upload a data series file."""
def upload(args):
   raise Exception('Upload is not implemented yet')

# Define top-level command line parser with global options and sub-command
# parsing enabled.
parser = argparse.ArgumentParser(prog='data_collector.py',
                                 description='Scan and upload inventory data',
                                 usage="%(prog)s [options]")
subparsers = parser.add_subparsers()
parser.add_argument('--verbose', 
                    action='store_true', 
                    help='Print verbose output')
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", "data_collector.log"))

# Define scan sub-parser.
parser_scan = subparsers.add_parser('scan', help='Scan data')
parser_scan.set_defaults(func=scan)
parser_scan.add_argument("--type",
                         help="Type of scan to run")
parser_scan.add_argument('--upload', 
                         action='store_true', 
                         help='If present upload scan file automatically')
parser_scan.add_argument("--config",
                         help="API credential file (default %(default)s)",
                         default="data_collector.ini")
parser_scan.add_argument("--out-dir",
                         help="Output directory (default: %(default)s)", 
                         default="data")

# Define upload sub-parser.
parser_upload = subparsers.add_parser('upload', 
                         help='Upload previously collected files')
parser_upload.set_defaults(func=upload)
parser_upload.add_argument('file', 
                           type=str, 
                           nargs='+', 
                           help='One or more files to upload')

# Parse arguments and execute subcommand.
args = parser.parse_args()

# Process options.  This will automatically print help and exit. 
args = parser.parse_args()

# Start logging.
init_logging(log_level=args.log_level, log_file=args.log_file)

# Process sub-command and return values.
rc = args.func(args)
sys.exit(rc)
