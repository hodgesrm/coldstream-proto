# Copyright (c) 2017-2018 Goldfin Systems LLC.  All rights reserved. 

"""Utility functions for cleaning scanned data"""

from decimal import Decimal
from datetime import datetime
import re


def is_valid_host(text: str):
    """Returns True if argument is a valid host name."""
    return is_valid_ip(text) or is_valid_dns_name(text)


def is_valid_ip(text: str):
    """Returns true if artument is a valid IPv4 address."""
    ip_regex = r'[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+'
    return re.match(ip_regex, text) is not None


def is_valid_dns_name(text: str):
    """Returns true if argument is a valid DNS name."""
    dns_regex = r'^((([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9]))$'
    return re.match(dns_regex, text) is not None


def clean_host_name(text: str, samples: list = None):
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
        if is_valid_host(
                text_without_spaces) and text_without_spaces in samples:
            return text_without_spaces

    # OK, we can't fix this unambiguously.  Let's just return.
    return text


def extract_date(date_string):
    """Extracts a date value from a string
    :param date_string: Date in any of a number of forms
    :type date_string: str
    :return: Date in YYYY-MM-DD format
    """
    # Strip whitespace.
    date_string = date_string.strip()

    # YYYY-MM-DD format. This is the default format so we just return the
    # cleaned-up date staring.
    yyyy_dash_mm_dash_dd = r'^.*?\s*([0-9]{4})-([0-9]{2})-([0-9]{2})'
    matcher = re.match(yyyy_dash_mm_dash_dd, date_string)
    if matcher:
        return date_string

    # MMM DD, YYYY format.
    mmm_dd_yyyy = r'^.*?\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\s*([0-9]+)\s*,\s*([0-9]+).*$'
    matcher = re.match(mmm_dd_yyyy, date_string.upper())
    if matcher:
        clean_date = "{m} {d}, {y}".format(m=matcher.group(1),
                                              d=matcher.group(2),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%b %d, %Y').strftime('%Y-%m-%d')

    # DD MMM YYYY format.
    dd_mmm_yyyy = r'^.*?\s*([0-9]+)\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\s*([0-9]+).*$'
    matcher = re.match(dd_mmm_yyyy, date_string.upper())
    if matcher:
        clean_date = "{d} {m} {y}".format(d=matcher.group(1),
                                              m=matcher.group(2),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%d %b %Y').strftime('%Y-%m-%d')

    # DD MMM YYYY format but with full month names.
    dd_fullmonth_yyyy = r'^.*?\s*([0-9]+)\s*(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER)\s*([0-9]+).*$'
    matcher = re.match(dd_fullmonth_yyyy, date_string.upper())
    if matcher:
        clean_date = "{d} {m} {y}".format(d=matcher.group(1),
                                              m=matcher.group(2),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%d %B %Y').strftime('%Y-%m-%d')

    # DD-MM-YYYY format.
    dd_dash_mm_dash_yyyy = r'^.*?\s*([0-9]{1,2})-([0-9]{1,2})-([0-9]{4})'
    matcher = re.match(dd_dash_mm_dash_yyyy, date_string)
    if matcher:
        clean_date = "{d}-{m}-{y}".format(d=matcher.group(1),
                                              m=matcher.group(2),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%d-%m-%Y').strftime('%Y-%m-%d')

    # MM/DD/YYYY format.
    dd_slash_mm_slash_yyyy = r'^.*?\s*([0-9]{1,2})\/([0-9]{1,2})\/([0-9]{4})'
    matcher = re.match(dd_slash_mm_slash_yyyy, date_string)
    if matcher:
        clean_date = "{d}-{m}-{y}".format(d=matcher.group(2),
                                              m=matcher.group(1),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%d-%m-%Y').strftime('%Y-%m-%d')

    # MM/DD/YY format.
    dd_slash_mm_slash_yy = r'^.*?\s*([0-9]{1,2})\/([0-9]{1,2})\/([0-9]{2})'
    matcher = re.match(dd_slash_mm_slash_yy, date_string)
    if matcher:
        clean_date = "{d}-{m}-{y}".format(d=matcher.group(2),
                                              m=matcher.group(1),
                                              y=matcher.group(3))
        return datetime.strptime(clean_date, '%d-%m-%y').strftime('%Y-%m-%d')

    # Other formats
    return None

def extract_currency(currency_string):
    """Extracts a date value from a string
    :param currency_string: Currency in any of a number of forms
    :type currency_string: str
    :return: Currency value with period as decimal point or None
    """
    # Strip whitespace.
    currency_string = currency_string.strip()

    # Currency number is a combination of numbers, commas, and periods with
    # two trailing decimal digits.  We'll extract from surrounding symbols.
    currency_regex1 = r'([0-9][0-9.,]*[.,]+[0-9][0-9]).*$'
    currency_regex2 = r'.*(\s|:|\$)([0-9][0-9.,]*[.,]+[0-9][0-9]).*$'
    matcher1 = re.match(currency_regex1, currency_string)
    if matcher1:
        raw_currency = matcher1.group(1)
    else:
        matcher2 = re.match(currency_regex2, currency_string)
        if matcher2:
            raw_currency = matcher2.group(2)
        else:
            raw_currency = None

    if raw_currency:
        if re.match(r'.*,[0-9][0-9]$', raw_currency):
            # Looks like decimal point is a comma. Strip periods and convert
            # comma to period.
            return raw_currency.replace(".", "").replace(",", ".")
        elif re.match(r'.*\.[0-9][0-9]$', raw_currency):
            # Looks as if period is the comma. Strip commas and return.
            return raw_currency.replace(",", "")
        else:
            # Not sure what this is, so just return it.
            return raw_currency
    else:
        return None

def extract_integer(integer_string):
    """Extracts an integer, filtering out common erroneous transformations

    :param integer_string: Candidate integer string
    :type integer_string: str
    :return: int value or None
    """
    # Strip whitespace.
    integer_string = integer_string.strip()

    # Transform any 'l' (ell) values to ones.
    integer_string = integer_string.replace("l", "1")

    # Check for validity.
    integer_regex = r'^[0-9]+$'
    matcher = re.match(integer_regex, integer_string)
    if matcher:
        return int(integer_string)
    else:
        return None
