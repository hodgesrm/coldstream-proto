# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Model of tabular documents"""

import hashlib
import logging
import re
import unittest

from goldfin_ocr.table.tabularmodel import TabularModel, Table, Page, Row, \
    Cell, Region, Line

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
            self.assertEqual(p, page.page_number)
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
            self.assertEqual(page_number, page.page_number)

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
            cell = Cell(c, page_number=13)
            cell.add_line(
                Line(text='a', region=Region((c - 1) * 100, 0, (c * 100) - 1, 25)))
            cell.add_line(
                Line('b', Region((c - 1) * 100, 26, (c * 100) - 1, 50)))
            cell.region = Region((c - 1) * 100, 0, (c * 100) - 1, 50)
            row.add_cell(cell)

        self.assertEqual(2, row.cell_count())
        cells = row.cells
        self.assertIsNotNone(cells)
        region = cells[0].region
        self.assertIsNotNone(region)
        self.assertEqual(13, cells[0].page_number)

        text_array = cells[0].text
        print(cells[0].lines)
        print(text_array)
        print(type(text_array))
        self.assertEqual(2, len(text_array))
        self.assertEqual('a', text_array[0])
        self.assertEqual('b', text_array[1])

    def test_lines(self):
        """Validate that we can set line content and append text"""
        line = Line('a', Region(100, 0, 110, 25), page_number=13)
        self.assertEqual('a', line.text)
        self.assertIsNotNone(line.region)
        self.assertEqual(13, line.page_number)

        line.append_text('b')
        self.assertEqual('ab', line.text)

    def test_regions(self):
        """Validate that we can create and compare regions"""
        r1 = Region(1, 1, 30, 30)
        r2 = Region(25, 25, 75, 75)
        r3 = Region(70, 70, 100, 100)
        r4 = Region(1, 1, 100, 100)

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

    def test_regions_intersection(self):
        """Validate that intersecting regions return a region and
        non-intersecting regions return None"""
        r1 = Region(10, 20, 30, 40)
        r2 = Region(20, 30, 40, 50)
        r3 = Region(30, 40, 50, 60)

        r1_r2 = r1.intersect(r2)
        self.assertIsNotNone(r1_r2)
        self.assertEqual(100, r1_r2.area())

        r2_r3 = r2.intersect(r3)
        self.assertIsNotNone(r2_r3)
        self.assertEqual(100, r2_r3.area())

        self.assertIsNone(r1.intersect(r3))

        self.assertTrue(r2.intersects(r1))
        self.assertTrue(r1.intersects(r1))
        self.assertFalse(r3.intersects(r1))

        r10 = Region(166, 3035, 415, 3065)
        r11 = Region(2168, 3033, 2385, 3072)
        self.assertIsNone(r10.intersect(r11))
        self.assertFalse(r11.intersects(r10))


if __name__ == '__main__':
    unittest.main()
