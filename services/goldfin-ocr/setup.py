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
    version="1.0.1",
    description="Goldfin Backend Services",
    install_requires=REQUIRES,
    packages=find_packages(),
    include_package_data=True,
    long_description="""\
    Python services for OCR and data series processing
    """,
    entry_points = {
        'console_scripts': [
            'datactl=goldfin_ocr.datactl', 
            'scanctl=goldfin_ocr.scanctl',
            'invoicectl=goldfin_ocr.table.invoicectl:exec_cmd'
         ]
    }
)
