#!/usr/bin/python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Factory class to pick a vendor-specific data processor class"""

from .providers.grand_coulee_data import GrandCouleeDataProcessor
from .providers.leaseweb_data import LeasewebDataProcessor
from goldfin_ocr.api.models.observation import Observation
import goldfin_ocr.vendors as vendors

def get_provider(observation):
    """Finds a provider class to process an observation.

    :param observation: An observation that is to be processd
    :type observation: Observation 

"""
    if observation.vendor_identifier == vendors.GRAND_COULEE:
        return GrandCouleeDataProcessor()
    elif observation.vendor_identifier == vendors.LEASEWEB:
        return LeasewebDataProcessor()
    else:
        return None
