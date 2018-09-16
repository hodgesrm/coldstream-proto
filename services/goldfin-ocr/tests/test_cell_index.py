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


class CellIndexTest(unittest.TestCase):
    def setUp(self):
        """Create test index"""
        self.index = tq.CellIndex()
        self.cells = []

        # Create cell with two lines.
        cell1 = tm.Cell(1, page_number=2)
        line1 = tm.Line()
        line1.page_number = 2
        line1.region = tm.Region(left=159, top=525, right=301, bottom=610)
        line1.text = "line2"
        cell1.add_line(line1)

        line2 = tm.Line()
        line2.page_number = 2
        line2.region = tm.Region(left=425, top=525, right=515, bottom=610)
        line2.text = "to_right_of_line2"
        cell1.add_line(line2)
        self.cells.append(cell1)

        cell2 = tm.Cell(2, page_number=2)
        line3 = tm.Line()
        line3.page_number = 3
        line3.region = tm.Region(left=425, top=525, right=515, bottom=610)
        line3.text = "line3"
        cell2.add_line(line3)
        self.cells.append(cell2)

        # Add cells to index. 
        for cell in self.cells:
            self.index.add_cell(cell)

    def test_cell_finds_lines(self):
        """Prove we can find cell for any line in a cell added to index
        """
        for cell in self.cells:
            for line in cell.lines:
                self.assertIsNotNone(self.index.get_cell(line), msg=line)

    def test_nonexistent_cell(self):
        """We don't find anything in the index if line is not enclosed in cell
        """
        new_line = tm.Line()
        self.assertIsNone(self.index.get_cell(new_line))
