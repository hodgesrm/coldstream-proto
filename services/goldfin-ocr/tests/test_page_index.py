#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Tests behavior of page location indexes"""
import json
import logging
import re
import unittest

import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.table.tablebuilder as tb
import goldfin_ocr.table.tabularquery as tq

# Define logger
logger = logging.getLogger(__name__)


class PageIndexTest(unittest.TestCase):
    def test_page_addition(self):
        """Adding an entity spawns a page node if it is missing
        """
        # Index starts with no pages.
        index = tq.PageIndex()
        self.assertEqual(0, len(index.pages()))

        # Has 2 pages after adding test data.
        for line in self._create_data():
            index.add(line)
        self.assertEqual(2, len(index.pages()))

    def test_missing_page_query(self):
        """We don't find anything in the index if page is not in it"""
        query = self._create_indexed_query()
        count = query.page(25).count()
        self.assertEqual(0, count)

    def test_missing_range(self):
        """We don't find anything if the index does not contain the range"""
        query1 = self._create_indexed_query()
        count = query1.x_range(1200, 1400).count()
        self.assertEqual(0, count)

        query2 = self._create_indexed_query()
        count = query2.y_range(1000, 1100).count()
        self.assertEqual(0, count)

    def test_missing_range_on_page(self):
        """We don't find anything if the range is not on the indicated page"""
        query = self._create_indexed_query()
        count = query.page(3).x_range(200, 300).count()
        self.assertEqual(0, count)

    def test_find_all(self):
        """We return all entities on all pages if using an enclosing range"""
        query = self._create_indexed_query()
        count = query.y_range(200, 1000).count()
        self.assertEqual(4, count)

    def test_find_limited_to_page(self):
        """We return only items on a single page if specified"""
        query = self._create_indexed_query()
        count = query.page(3).x_range(400, 600).count()
        self.assertEqual(1, count)

    def test_find_in_x_range(self):
        """We find all entities in x range across pages"""
        query = self._create_indexed_query()
        count = query.x_range(400, 500).count()
        query2 = self._create_indexed_query()
        self.assertEqual(2, count)

    def test_find_in_y_range(self):
        """We find all items in y range on a single page if specified"""
        query = self._create_indexed_query()
        count = query.y_range(500, 599).page(2).count()
        self.assertEqual(2, count)


    def test_query_row(self):
        """Validates that we can find entries in the same row"""

    def _create_indexed_query(self):
        index = tq.PageIndex()
        for line in self._create_data():
            index.add(line)
        return tq.IndexedQuery(index)

    def _create_data(self):
        data = []
        # Test data.
        line1 = tm.Line()
        line1.page_number = 2
        line1.region = tm.Region(left=159, top=525, right=301, bottom=610)
        line1.text = "line2"
        data.append(line1)

        line2 = tm.Line()
        line2.page_number = 2
        line2.region = tm.Region(left=425, top=525, right=515, bottom=610)
        line2.text = "to_right_of_line2"
        data.append(line2)

        line3 = tm.Line()
        line3.page_number = 3
        line3.region = tm.Region(left=425, top=525, right=515, bottom=610)
        line3.text = "line3"
        data.append(line3)

        line4 = tm.Line()
        line4.page_number = 2
        line4.region = tm.Region(left=150, top=640, right=302, bottom=695)
        line4.text = "below_line2"
        data.append(line4)

        return data
