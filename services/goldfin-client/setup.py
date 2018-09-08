# Copyright (c) 2018 Goldfin Systems

"""
Goldfin Intelligent Invoice Processing
"""

import sys
from setuptools import setup, find_packages

# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

setup(
    name="goldfin_client",
    version="1.0.2",
    description="Goldfin Client Tools",
    install_requires=[
        'requests>=2.19.1',
        'six>=1.11.0',
        'pyyaml==3.13'
    ],
    packages=find_packages(),
    include_package_data=True,
    long_description="""\
    Goldfin toolset for inventory scanning.
    """,
    entry_points = {
        'console_scripts': ['data_collector=goldfin.client.data_collector']
    }
)
