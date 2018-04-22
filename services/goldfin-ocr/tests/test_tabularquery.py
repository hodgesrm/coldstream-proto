#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Tests ability to issue queries on tabular data"""
import json
import logging
import re
import unittest

import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.table.tablebuilder as tb
import goldfin_ocr.table.tabularquery as tq

# Define logger
logger = logging.getLogger(__name__)


class TabularQueryTest(unittest.TestCase):
    inap_xml = "/home/rhodges/coldstream/abbyy/code/ocrsdk.com/Bash_cURL/processing/inap/Invoice-14066-486955.xml"

    @classmethod
    def setUpClass(self):
        """Read XML and build model once for all test cases"""
        print("Running setup")
        with open(self.inap_xml, "rb") as xml_file:
            xml = xml_file.read()
        logger.info("Reading data " + self.inap_xml)
        self.model = tb.build_model(xml)

    def setUp(self):
        # logger.info(self._dump_to_json(self.model))
        pass

    def test_custom_predicate(self):
        """Validates that we can add a custom predicate to select data"""
        # Load model in the query engine.
        engine = tq.QueryEngine(self.model)

        # Find only lines with 'a' in the text.
        regex = r'^.*a'

        def custom_predicate(entity):
            if not isinstance(entity, tm.Line):
                return False
            if re.match(regex, entity.text) is None:
                return False
            return True

        lines = engine.tabular_query().predicate(custom_predicate).generate()
        count = 0
        for line in lines:
            self.assertIsNotNone(re.match(regex, line.text), line.text)
            count += 1

        self.assertGreater(count, 0)

    def test_query_on_pages(self):
        """Validates that we can find pages from the tabular model"""

        # Load model in the query engine.
        engine = tq.QueryEngine(self.model)

        # Find only pages using cut.
        pages = engine.tabular_query().matches_type(tm.Page).cut().generate()
        page_list = list(pages)
        self.assertGreater(len(page_list), 0, "Found at least one item")
        for page in pages:
            self.assertTrue(isinstance(page, tm.Page), "Every item is a page")

        # Count pages ensure it matches our list count.
        page_count = engine.tabular_query().matches_type(tm.Page).cut().count()
        self.assertEqual(page_count, len(page_list))

        # Ensure we can find the entities under a particular page when cut is
        # not included.
        page_1_entities = engine.tabular_page_query(1).generate()
        page_1_list = list(page_1_entities)
        self.assertEqual(len(page_1_list), 1,
                         "Found page 1 and items under it")
        self.assertEqual(page_1_list[0], page_list[0],
                         "First item is first page")

    def test_query_on_lines(self):
        """Validates that we can find lines from the tabular model"""

        # Load model in the query engine.
        engine = tq.QueryEngine(self.model)

        # Find all lines in the model and store the line that has 'Internap Corporation' in it.
        all_lines = engine.tabular_query().matches_type(tm.Line).generate()
        self.assertIsNotNone(all_lines)

        count = 0
        total_line = None
        for line in all_lines:
            count += 1
            if line.matches(r'^Invoice Total'):
                total_line = line

        # Confirm we found more than 0 lines and that we got the total line.
        logger.info("Found {0} lines".format(count))
        self.assertGreater(count, 0)
        self.assertIsNotNone(total_line)
        logger.info("Test line: {0}".format(total_line))

        # Now find the total line again using an intersect operation.
        total_line_intersections = engine.tabular_query().matches_type(
            tm.Line).page(total_line.page_number).intersects(
            total_line.region).generate()

        # Confirm we got one and that it's the same as the previous one.
        intersect_list = list(total_line_intersections)
        self.assertEqual(len(intersect_list), 1)
        self.assertEqual(total_line, intersect_list[0])

    def test_index_query_on_lines(self):
        """Validates that we can find lines using page location index"""

        # Load model in the query engine.
        engine = tq.QueryEngine(self.model)

        # Find all lines in the model and store the total line.
        all_lines = engine.indexed_query().matches_type(tm.Line).generate()
        self.assertIsNotNone(all_lines)

        count = 0
        total_line = None
        for line in all_lines:
            count += 1
            if line.matches(r'^Invoice Total'):
                total_line = line

        # Confirm we found more than 0 lines and that we got the total line.
        logger.info("Found {0} lines".format(count))
        self.assertGreater(count, 0)
        self.assertIsNotNone(total_line)
        logger.info("Test line: {0}".format(total_line))

        # Now find the total line again using its left edge.
        total_line_intersections = engine.indexed_query().matches_type(
            tm.Line).page(total_line.page_number).x_range(
            total_line.region.left - 10,
            total_line.region.left + 10).intersects(
            total_line.region).generate()

        # Confirm we got one and that it's the same as the previous one.
        intersect_list = list(total_line_intersections)
        self.assertEqual(len(intersect_list), 1)
        self.assertEqual(total_line, intersect_list[0])

        # Repeat the previous test but use the top edge in the range.
        total_line_intersections = engine.indexed_query().matches_type(
            tm.Line).page(total_line.page_number).y_range(
            total_line.region.top - 10,
            total_line.region.top + 10).intersects(
            total_line.region).generate()

        intersect_list = list(total_line_intersections)
        self.assertEqual(len(intersect_list), 1)
        self.assertEqual(total_line, intersect_list[0])

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)
