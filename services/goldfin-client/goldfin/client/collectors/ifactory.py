# Copyright (c) 2018 Goldfin Systems, LLC.  All rights reserved. 

"""Factory to pick an inventory processor class"""
from .leaseweb_inventory import LeasewebProcessor


def get_provider(collector, params):
    """Finds a provider class for a particular collector name

    :param collector: Collector key
    :type collector: str
    """
    if collector == "leaseweb":
        return LeasewebProcessor(**params)
    else:
        raise Exception("Unknown collector")
