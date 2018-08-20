# Copyright (c) 2018 Goldfin Systems, LLC.  All rights reserved. 

"""Factory to pick an inventory processor class"""
from .leaseweb_inventory import LeasewebProcessor


def get_provider(provider, params):
    """Finds a provider class for a particular probe name

    :param provider: Provider key
    :type provider: str
    :param params: Provider parameters
    :type params: dict
    """
    if provider == "leaseweb":
        return LeasewebProcessor(**params)
    else:
        raise Exception("Unknown provider: {0}".format(collector))
