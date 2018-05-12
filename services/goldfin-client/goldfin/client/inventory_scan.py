# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Inventory analysis client"""

import argparse
import configparser
import json
import logging
import os
import sys

from goldfin.client.inventory.ifactory import get_provider

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

def print_info(msg):
    logger.info(msg)
    print(msg)

def print_error(msg):
    logger.error(msg)
    print(msg)

# Define top-level command line parsing.
parser = argparse.ArgumentParser(prog='inventory_scan.py',
                                 usage="%(prog)s [options]")
parser.add_argument("--inventory",
                    help="Type of inventory scan to run")
parser.add_argument("--config",
                    help="Configuration containing inventory access credentials",
                    default="inventory.ini")
parser.add_argument("--out-dir",
                    help="Output directory for observations", 
                    default="data")
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", "inventory.log"))
parser.add_argument("--verbose",
                    help="Log verbose information", 
                    action="store_true", default=False)

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Start logging.
init_logging(log_level=args.log_level, log_file=args.log_file)

# Ensure we have inventory type and can find implementation class. 
if args.inventory is None:
    print_error("You must specify an inventory type with --inventory")
    sys.exit(1)

# Ensure we have a writable output director
if args.out_dir is None:
    print_error("You must specify an output directory --out-dir")
    sys.exit(1)
os.makedirs(args.out_dir, exist_ok=True)
if not os.path.exists(args.out_dir) or not os.access(args.out_dir, os.W_OK):
    print_error("Output directory does not exist or is not writable: {0}".format(args.out_dir))
    sys.exit(1)

# Load config for inventory 
if os.path.exists(args.config):
    config_parser = configparser.ConfigParser()
    with open(args.config, "r") as config_file:
        config_parser.read_file(config_file)
    section = config_parser[args.inventory]
    params = {}
    for key in section:
        params[key] = section[key]
    provider = get_provider(args.inventory, params)
    obs = provider.execute()
    name = "{0}-{1}".format(obs.vendor, obs.effective_date)
    output_file = os.path.join(args.out_dir, name)
    with open(output_file, "w") as obs_file:
        print_info("Writing observation to output file: {0}".format(output_file))
        json.dump(obs.to_dict(), obs_file)
else:
    print_error("Config file does not exist: {0}".format(args.config))
    sys.exit(1)

