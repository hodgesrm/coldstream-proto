# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions for cleaning scanned data"""

from decimal import Decimal
from datetime import datetime
import re

def is_valid_host(text:str):
    return is_valid_ip(text) or is_valid_dns_name(text)

def is_valid_ip(text:str):
    ip_regex = r'[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+'
    return re.match(ip_regex, text) is not None

def is_valid_dns_name(text:str):
    dns_regex = r'^((([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9]))$'
    return re.match(dns_regex, text) is not None

def clean_host_name(text:str, samples:list=None):
    """Takes a 'dirty' host name + optional likely 'clean' names
    and return a fixed up, valid host name
    """
    # Strip leading and trailing whitespace, then see if that's
    # good enough.
    text = text.strip()
    if is_valid_host(text):
        return text

    # If there are spaces in the name see if removing spaces
    # or substituting periods matches a sample.
    if re.search(r' ', text) and samples is not None:
        text_with_periods = text.replace(' ', '.')
        if is_valid_host(text_with_periods) and text_with_periods in samples:
            return text_with_periods

        text_without_spaces = text.replace(' ', '')
        if is_valid_host(text_without_spaces) and text_without_spaces in samples:
            return text_without_spaces

    # OK, we can't fix this unambiguously.  Let's just return.
    return text