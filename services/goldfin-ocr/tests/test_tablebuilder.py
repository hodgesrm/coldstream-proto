#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import io
import json
import logging
from lxml import etree
import unittest

import goldfin_ocr.table.tabularmodel as tm
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
        def ovh_predicate(block):
            return (len(block.select_text(r'OVH\.com')) > 0)

        ovh_com_blocks = model.select_blocks(ovh_predicate)
        self.assertTrue(len(ovh_com_blocks) == 1)
        logger.info(self._dump_to_json(ovh_com_blocks))

        # Ensure we can find the total by finding a block with "TOTAL" then
        # finding a block that overlaps horizontally.
        def total_predicate(block):
            return (len(block.select_text(r'^TOTAL$')) > 0)

        total_blocks = model.select_blocks(total_predicate)
        logger.info(self._dump_to_json(total_blocks))
        self.assertTrue(len(total_blocks) == 1)
        total_region = total_blocks[0].region

        def total_value_predicate(block):
            return (block.region.overlaps_vertically(total_region) and
                    block != total_blocks[0])

        total_value_blocks = model.select_blocks(total_value_predicate)
        logger.info(self._dump_to_json(total_value_blocks))
        self.assertTrue(len(total_blocks) == 1)

    def test_build_2_large(self):
        """Validate that we can set up a model from a multi-page invoice from Internap"""
        with open(self.inap_xml, "rb") as xml_file:
            xml = xml_file.read()
        logger.info("Reading data " + self.inap_xml)

        model = tb.build_model(xml)
        self.assertIsNotNone(model)
        logger.info(self._dump_to_json(model))

        # Ensure we find a block with Internap Corporation in it.
        def inap_predicate(block):
            return (len(block.select_text(r'Internap Corporation')) > 0)

        inap_blocks = model.select_blocks(inap_predicate)
        self.assertTrue(len(inap_blocks) == 1)
        logger.info(self._dump_to_json(inap_blocks))

        # Ensure we can find the total by finding a block with "Invoice Total"
        # then finding a block that overlaps horizontally.
        def total_predicate(block):
            return (len(block.select_text(r'^Invoice Total')) > 0 and block.region.page_number == 1)

        total_blocks = model.select_blocks(total_predicate)
        logger.info(self._dump_to_json(total_blocks))
        self.assertTrue(len(total_blocks) == 1)
        total_region = total_blocks[0].region

        def total_value_predicate(block):
            return (block.region.overlaps_vertically(total_region) and
                    block != total_blocks[0] and
                    block.region.is_to_right_of(total_region))

        total_value_blocks = model.select_blocks(total_value_predicate)
        logger.info(self._dump_to_json(total_value_blocks))
        self.assertTrue(len(total_value_blocks) == 1)

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)
