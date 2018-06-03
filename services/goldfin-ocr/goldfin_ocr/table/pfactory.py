#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Factory class to pick a provider translator class"""
from .providers.grandcoulee import GrandCouleeProcessor
from .providers.inap import InapProcessor
from .providers.leaseweb import LeasewebProcessor
from .providers.ovh import OvhProcessor


def get_provider(tabular_model):
    """Finds a provider translation class for a particular tabular model"""
    if GrandCouleeProcessor.conforms(tabular_model):
        return GrandCouleeProcessor(tabular_model)
    elif InapProcessor.conforms(tabular_model):
        return InapProcessor(tabular_model)
    elif LeasewebProcessor.conforms(tabular_model):
        return LeasewebProcessor(tabular_model)
    elif OvhProcessor.conforms(tabular_model):
        return OvhProcessor(tabular_model)
    else:
        return None