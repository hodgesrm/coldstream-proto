# Copyright (c) 2018 Robert Hodges 

"""
    Goldfin Intelligent Invoice Processing
"""

import sys
from setuptools import setup, find_packages

NAME = ""
VERSION = "1.0.0"
# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

REQUIRES = ["boto3", "urllib3 >= 1.15", "six >= 1.10", "certifi", "python-dateutil"]

setup(
    name="goldfin_ocr",
    version="0.1",
    description="Goldfin OCR logic",
    install_requires=REQUIRES,
    packages=find_packages(),
    include_package_data=True,
    long_description="""\
    Public REST API for managing IT service invoices and inventory
    """
)
