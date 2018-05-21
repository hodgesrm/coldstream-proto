#!/usr/bin/python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Factory class to pick a vendor-specific data processor class"""

import goldfin_ocr.vendors as vendors
from .providers.leaseweb_data import LeasewebDataProcessor
from goldfin_ocr.api.models.observation import Observation

def get_provider(observation):
    """Finds a provider class that can process an observation
       to one or more results.

    :param observation: An observation that is to be processes
    :type observation: Observation 

"""
    if observation.vendor_identifier == vendors.LEASEWEB:
        return LeasewebDataProcessor()
    else:
        return None
