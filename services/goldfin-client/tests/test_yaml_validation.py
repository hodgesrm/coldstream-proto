#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Tests behavior of page location indexes"""
import logging
import os
import re
import unittest

from goldfin.client.yaml_config_loader import load_yaml, YamlValidationError

# Define logger
logger = logging.getLogger(__name__)


class YamlValidatorTest(unittest.TestCase):
    def setUp(self):
        """Find current source location and locate test files."""
        self.here = os.path.dirname(os.path.abspath(__file__))
        self.data = os.path.join(self.here, "data")
        if not os.path.exists(self.data):
            raise Exception("Required data directory not found: {0}".format(self.data))

    def test_basic_ok(self):
        """Show we can validate legal config with single probe and no tags
        """
        basic_config_path = os.path.join(self.data, 'basic_config.yaml')
        basic_config = load_yaml(basic_config_path)
        self.assertIsNotNone(basic_config)
