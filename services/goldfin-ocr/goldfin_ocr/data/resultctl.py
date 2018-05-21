#!/usr/bin/env python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Observation to result processing command line interface (CLI)"""

# Standard Python libraries. 
import argparse
import logging
import os

from goldfin_ocr.json_xlate import json_dict_to_model
from goldfin_ocr.data import pfactory
import goldfin_ocr.util as util

# Standard logging initialization.
logger = logging.getLogger(__name__)


def process_observation(args):
    """Process a single request using command line arguments

    :param args: Argparse arguments
    """

    # Read in the JSON observation. 
    with open(args.obs, "r") as obs_file:
        obs = obs_file.read()
        observation = json_dict_to_model(json.loads(ds), Observation)

    # Select a result processing provider.
    provider = pfactory.get_provider(observation)
    if provider is None:
        raise Exception(
              "Unable to find provider for observation: vendor={0}".format(
                 observation.vendor_identifier))

    # Translate observation to result and print output.
    logger.info("Fetching observation content")
    result = provider.get_content(observation)
    logger.info(util.dump_to_json(result))

#############################################################################
# Command line processor
#############################################################################

# Define top-level command line parsing.
parser = argparse.ArgumentParser(prog='resultctl.py',
                                 usage="%(prog)s [options]")
parser.add_argument("--obs",
                    help="Location of observation JSON file")
parser.add_argument("--log-level",
                    help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                    default="INFO")
parser.add_argument("--log-file",
                    help="Name of log file (default: %(default)s)",
                    default=os.getenv("LOG_FILE", "resultctl.log"))

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Start logging.
util.init_logging(log_level=args.log_level, log_file=args.log_file)

# Process the observation.
process_observation(args)

print("Done!!!")
