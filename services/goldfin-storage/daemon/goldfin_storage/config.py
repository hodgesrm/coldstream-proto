# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

import configparser
import os
import logging

# Our logger.
logger = logging.getLogger(__name__)

def load(file):
    """Load configuration file"""
    if file:
        logger.info("Loading configuration: {0}".format(file))
        if not os.path.exists(file):
            raise Exception(
                "ERROR: storage configuration definition file missing or unreadable: {0}"
                    .format(file))
        config = configparser.ConfigParser()
        successfully_read = config.read(file)
        if len(successfully_read) == 0:
            raise Exception(
                "Unable to read configuration file: {0}".format(file))
        return config
    else:
        raise Exception("Must provide a file path")