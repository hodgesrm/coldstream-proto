# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Inventory analysis client"""

import argparse
import configparser
import json
import logging
import os
import sys
import yaml

from goldfin.client.json_xlate import SwaggerJsonEncoder
from goldfin.client.collectors.ifactory import get_provider

import time
import goldfin.client.api as api
from goldfin.client.api.rest import ApiException
from goldfin.client.api.models import TagSet
from goldfin.client.api.models import Tag
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
def make_tagset(tags_dict):
    """Creates tag set from a dictionary"""
    tagSet = []
    for k,v in tags_dict.items():
        tagSet.append(Tag(name=k, value=v))
    return tagSet

def do_file_upload(file, tags, host=None, port=443, secret_key=None, 
                   verify_ssl=True):
    # Set client configuration variables. 
    url = 'https://{0}:{1}/api/v1'.format(host, port)
    api.configuration.host = url
    api.configuration.api_key['vnd.io.goldfin.apikey'] = secret_key
    api.configuration.verify_ssl = verify_ssl

    try:
        # Upload document
        api_instance = api.InventoryApi()
        description = 'description_example' 
        encoder = SwaggerJsonEncoder()
        tagsAsJson = encoder.encode(tags)
        api_response = api_instance.data_create(file,
                                                description=description,
                                                tags=tagsAsJson)
        pprint(api_response)
    except ApiException as e:
        print("Exception when calling DocumentApi->document_create: %s\n" % e)

"""Perform a data probe and optional upload resulting data series file."""
def run_probes(args):
    # Load config file. 
    if not os.path.exists(args.config):
        print_error("Config file does not exist: {0}".format(args.config))
        return 1
    with open(args.config, "r") as config_file:
        config = yaml.load(config_file)

    # Ensure we have a writable output directory
    if args.out_dir is None:
        print_error("You must specify an output directory --out-dir")
        return 1
    os.makedirs(args.out_dir, exist_ok=True)
    if not os.path.exists(args.out_dir) or not os.access(args.out_dir, os.W_OK):
        print_error("Output directory does not exist or is not writable: {0}".format(args.out_dir))
        return 1

    # Pull out top-level sections of the configuration file. 
    api_server = config['api_server']
    general_tags = config['tags']
    probes = config['data_probes']

    # Decide which probes to run. 
    if args.all_probes:
        probe_list = probes
    elif args.probes:
        probe_list = []
        for name in args.probes.split(","):
            if probes.get(name) is None:
                raise Exception("Unknown probe name: {0}".format(name))
            probe_list.append(probes[name])
    else:
        raise Exception("No probes specified")
    
    # Now iterate over the probes and execute each in sequence. 
    for probe in probe_list:
        name = probe['name']
        provider = probe['provider']
        provider_params = probe['provider_params']
        tags = probe['tags']
        # Find and execute probe.
        provider = get_provider(provider, provider_params)
        obs = provider.execute()
        # Construct and assign tag set. 
        obs.tags = make_tagset(tags)
 
        # Write the output.     
        name = "{0}-{1}.json".format(obs.vendor_identifier, obs.effective_date.strftime("%Y-%m-%d_%H:%M:%S"))
        output_file = os.path.join(args.out_dir, name)
        with open(output_file, "w") as obs_file:
            print_info("Writing observation to output file: {0}".format(output_file))
            encoder = SwaggerJsonEncoder()
            content = encoder.encode(obs)
            obs_file.write(content)

        # Optionally load file to Goldfin.
        if args.upload:
            do_file_upload(output_file, make_tagset(general_tags), **api_server)

    return 0
    
"""Upload a data series file."""
def upload(args):
   raise Exception('Upload is not implemented yet')

# Define top-level command line parser with global options and sub-command
# parsing enabled.
parser = argparse.ArgumentParser(prog='data_collector.py',
                                 description='Collect and upload observation data',
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

# Define run command sub-parser.
parser_run = subparsers.add_parser('run', help='Execute data probes')
parser_run.set_defaults(func=run_probes)
parser_run.add_argument("--probes",
                        help="Comma-separated list of one or more probe names")
parser_run.add_argument("--all-probes",
                        action='store_true', 
                        help="If present execute all probes")
parser_run.add_argument('--upload', 
                        action='store_true', 
                        help='If present upload observation files automatically')
parser_run.add_argument("--config",
                        help="Configuration file (default %(default)s)",
                        default="data_config.yaml")
parser_run.add_argument("--out-dir",
                        help="Observation output directory (default: %(default)s)", 
                        default="data")

# Define upload sub-parser.
parser_upload = subparsers.add_parser('upload', 
                         help='Upload previously collected observation files')
parser_upload.set_defaults(func=upload)
parser_upload.add_argument('file', 
                           type=str, 
                           nargs='+', 
                           help='One or more files to upload')
parser_upload.add_argument("--config",
                           help="Configuration file (default %(default)s)",
                           default="data_config.yaml")

# Parse arguments and execute subcommand.
args = parser.parse_args()

# Process options.  This will automatically print help and exit. 
args = parser.parse_args()

# Start logging.
init_logging(log_level=args.log_level, log_file=args.log_file)

# Process sub-command and return values.
rc = args.func(args)
sys.exit(rc)
