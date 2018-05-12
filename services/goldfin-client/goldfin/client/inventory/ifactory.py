# Copyright (c) 2018 Goldfin Systems, LLC.  All rights reserved. 

"""Factory to pick an inventory processor class"""
from .leaseweb_inventory import LeasewebProcessor


def get_provider(vendor, params):
    """Finds a provider processor class for a particular vendor name

    :param vendor: Vendor key
    :type vendor: str
    """
    if vendor == "leaseweb":
        return LeasewebProcessor(**params)
    else:
        raise Exception("Unknown vendor")
