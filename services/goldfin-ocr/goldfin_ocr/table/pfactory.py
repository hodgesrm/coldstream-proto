#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Factory class to pick a provider translator class"""
from table.providers.inap import InapProcessor
from table.providers.ovh import OvhProcessor


def get_provider(tabular_model):
    """Finds a provider translation class for a particular tabular model"""
    if InapProcessor.conforms(tabular_model):
        return InapProcessor(tabular_model)
    elif OvhProcessor.conforms(tabular_model):
        return OvhProcessor(tabular_model)
    else:
        return None
