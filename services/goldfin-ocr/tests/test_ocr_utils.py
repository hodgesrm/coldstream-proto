#!/usr/bin/python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Tests printing functions"""
import logging
import os
import unittest
import uuid

import goldfin_ocr.printing as printing

# Define logger
logger = logging.getLogger(__name__)


class TestPrinting(unittest.TestCase):
    def setUp(self):
        """Find current source location and locate test files."""
        self.here = os.path.dirname(os.path.abspath(__file__))
        self.data = os.path.join(self.here, "data")
        if not os.path.exists(self.data):
            raise Exception(
               "Required data directory not found: {0}".format(self.data))

    def test_printing(self):
        """Confirm we can print a PDF file"""
        output_path = '/tmp/print-test-' + str(uuid.uuid4()) + '.pdf'
        if os.path.exists(output_path):
            os.remove(output_path)
        test_pdf = os.path.join(self.data, 'GC-Hosting-GC2018-06-251.pdf')

        printer = printing.pdf_printer()
        self.assertIsNotNone(printer)
        printer.print(test_pdf, output_path)

        # Ensure file appears and remove evidence.
        self.assertTrue("Looking for pdf output: {0}".format(output_path),
            os.path.exists(output_path))
        os.remove(output_path)
