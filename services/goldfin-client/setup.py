# Copyright (c) 2018 Robert Hodges 

"""
    Goldfin Intelligent Invoice Processing
"""

import sys
from setuptools import setup, find_packages

NAME = "goldfin_client"
VERSION = "1.0.0"
# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

REQUIRES = ["requests"]

setup(
    name="goldfin_client",
    version="0.1",
    description="Goldfin Client Tools",
    install_requires=REQUIRES,
    packages=find_packages(),
    include_package_data=True,
    long_description="""\
    Goldfin toolset for inventory scanning
    """,
    entry_points = {
        'console_scripts': ['inventory_scan=goldfin.client.inventory_scan']
    }
)
