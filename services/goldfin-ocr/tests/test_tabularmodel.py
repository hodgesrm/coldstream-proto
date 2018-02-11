# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Model of tabular documents"""

import hashlib
import logging
import re
import unittest

from goldfin_ocr.table.tabularmodel import TabularModel, Table, Page, Row, Cell, Region

# Define logger
logger = logging.getLogger(__name__)

class TableModelTest(unittest.TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_pages(self):
        """Validate that we can set up a model with pages"""
        model = TabularModel()
        for p in range(1, 4):
            page = Page(p)
            self.assertEqual(p, page.number)
            logger.info("Adding: " + str(page.__dict__))
            model.add_page(page)

        # Check page counts.
        pages = model.pages
        self.assertIsNotNone(pages)
        self.assertEqual(3, model.page_count)

        # Check prev/next relationships.
        page_number = 0
        for page in pages:
            page_number += 1
            logger.info("Verifying: " + str(page.__dict__))
            self.assertEqual(page_number, page.number)

    def test_tables(self):
        """Validate that we can add tables to pages"""
        page = Page(2)
        for t in range(2):
            table = Table()
            page.add_table(table)

        tables = page.tables
        self.assertIsNotNone(tables)
        self.assertEqual(2, len(tables))

    def test_rows(self):
        """Validate that we can create rows and add them to tables"""
        table = Table()
        r1 = Row()
        r2 = Row()
        table.add_row(r1)
        table.add_row(r2)

        self.assertEqual(2, table.row_count())
        self.assertIsNotNone(table.rows)
        self.assertEqual(r1, table.header_row())

    def test_cells(self):
        """Validate that we can add cells to a row"""
        row = Row()
        for c in range(1, 3):
            cell = Cell(c)
            cell.add_text('a')
            cell.add_text('b')
            cell.region = Region(13, (c - 1) * 100, 0, (c * 100) - 1, 50)
            row.add_cell(cell)

        self.assertEqual(2, row.cell_count())
        cells = row.cells
        self.assertIsNotNone(cells)
        region = cells[0].region
        self.assertIsNotNone(region)
        self.assertEqual(13, region.page_number)

    def test_regions(self):
        """Validate that we can create and compare regions"""
        r0 = Region(1, 1, 1, 100, 100)
        r1 = Region(2, 1, 1, 30, 30)
        r2 = Region(2, 25, 25, 75, 75)
        r3 = Region(2, 70, 70, 100, 100)
        r4 = Region(2, 1, 1, 100, 100)

        self.assertFalse(r0.contains(r2))
        self.assertFalse(r0.overlaps_vertically(r1))
        self.assertFalse(r0.overlaps_horizontally(r2))

        self.assertTrue(r4.contains(r1))
        self.assertTrue(r4.contains(r2))
        self.assertTrue(r4.contains(r4))
        self.assertFalse(r2.contains(r3))

        self.assertTrue(r1.overlaps_horizontally(r2))
        self.assertTrue(r1.overlaps_vertically(r2))
        self.assertTrue(r1.overlaps_horizontally(r4))
        self.assertTrue(r4.overlaps_vertically(r3))
        self.assertFalse(r1.overlaps_horizontally(r3))
        self.assertFalse(r1.overlaps_vertically(r3))


if __name__ == '__main__':
    unittest.main()
