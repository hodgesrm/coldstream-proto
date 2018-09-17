#!/usr/bin/python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

import logging
import os
import unittest
import uuid

import goldfin_ocr.util as util

# Define logger
logger = logging.getLogger(__name__)

class TestUtils(unittest.TestCase):
    """Tests utility functions"""

    def test_gzip(self):
        """Validate round trip gzip serialization/deserialization"""
        string_value = "abcdefghijklmnopqrstuvwxyz!!!"
        gzip = util.string_to_gzipped_utf8(string_value)
        self.assertIsNotNone(gzip)

        roundtrip_value = util.gzipped_utf8_to_string(gzip)
        self.assertIsNotNone(roundtrip_value)
        self.assertEqual(string_value, roundtrip_value)
