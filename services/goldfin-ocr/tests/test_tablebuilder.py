#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import io
import json
import logging
from lxml import etree
import unittest

import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.table.tabularquery as tq
import goldfin_ocr.table.tablebuilder as tb

# Define logger
logger = logging.getLogger(__name__)


class TableBuilderTest(unittest.TestCase):
    ovh_xml = "/home/rhodges/coldstream/abbyy/code/ocrsdk.com/Bash_cURL/processing/ovh/invoice_WE666184.xml"
    inap_xml = "/home/rhodges/coldstream/abbyy/code/ocrsdk.com/Bash_cURL/processing/inap/Invoice-14066-486955.xml"

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_build_1_small(self):
        """Validate that we can set up a model from a single-page invoice from OVH"""
        with open(self.ovh_xml, "rb") as xml_file:
            xml = xml_file.read()

        model = tb.build_model(xml)
        self.assertIsNotNone(model)

        # Ensure we find a block with OVH.com in it.
        engine = tq.QueryEngine(model)
        ovh_com_count = engine.tabular_line_query().matches_regex(
            r'OVH\.com').count()
        self.assertTrue(ovh_com_count == 1)

        # Ensure we can find the total by finding a block with "TOTAL" then
        # finding a block that overlaps horizontally.
        total = engine.tabular_line_query().matches_regex(r'^TOTAL$').first()
        overlapping_count = engine.indexed_query().page(
            total.page_number).matches_type(tm.Line).is_to_right_of(
            total.region).count()
        self.assertTrue(overlapping_count == 1)

    def test_build_2_large(self):
        """Validate that we can set up a model from a multi-page invoice from Internap"""
        with open(self.inap_xml, "rb") as xml_file:
            xml = xml_file.read()
        logger.info("Reading data " + self.inap_xml)

        model = tb.build_model(xml)
        self.assertIsNotNone(model)
        logger.info(self._dump_to_json(model))

        # Ensure we find a block with Internap Corporation in it.
        engine = tq.QueryEngine(model)
        inap_count = engine.tabular_line_query().matches_regex(
            r'Internap Corporation').count()
        self.assertTrue(inap_count == 1)

        # Ensure we can find the total by finding a block with "Invoice Total"
        # then finding a block that overlaps horizontally.
        total = engine.tabular_line_query().matches_regex(r'^Invoice Total').first()
        overlapping_count = engine.tabular_line_query().page(
            total.page_number).is_to_right_of(total.region).count()
        self.assertTrue(overlapping_count == 1)

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)
