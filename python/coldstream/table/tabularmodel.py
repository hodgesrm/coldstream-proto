# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Model of tabular documents"""

import hashlib
import logging
import re
import unittest

# Define logger
logger = logging.getLogger(__name__)

class TabularModel:
    """Root of a page-oriented table model"""

    def __init__(self):
        self._pages = {}

    def add_page(self, page):
        self._pages[page.number] = page

    @property
    def pages(self):
        """Returns array of pages"""
        pages = []
        for page_number in sorted(self._pages):
            pages.append(self._pages[page_number])
        return pages

    @property
    def page_count(self):
        return len(self._pages.keys())

    def select_blocks(self, predicate=None):
        """Return all blocks that match the predicate or all blocks if predicate is None"""
        blocks = []
        for page in self.pages:
            blocks += page.select_blocks(predicate)
        return blocks

    def select_tables(self, predicate=None):
        """Return all tables that match the predicate or all tables if predicate is None"""
        tables = []
        for page in self.pages:
            tables += page.select_tables(predicate)
        return tables

class Page:
    """Denotes a page in the document, which may contain one or more tables"""

    def __init__(self, number):
        self._number = number
        self._tables = []
        self._blocks = []

    @property
    def number(self):
        return self._number

    def add_table(self, table):
        self._tables.append(table)

    @property
    def tables(self):
        return self._tables

    def add_block(self, block):
        self._blocks.append(block)

    @property
    def blocks(self):
        return self._blocks

    def select_blocks(self, predicate):
        blocks = []
        for block in self._blocks:
            if predicate is None or predicate(block):
                blocks.append(block)
        return blocks

    def select_tables(self, predicate):
        tables = []
        for table in self._tables:
            if predicate is None or predicate(table):
                tables.append(table)
        return tables


class Region:
    """Defines an area within a scanned document using the page number
    and pixel coordinates.  Regions can be thought of as a set for pixels"""

    def __init__(self, page_number, left, top, right, bottom):
        self._page_number = page_number
        self._left = left
        self._top = top
        self._right = right
        self._bottom = bottom

    @property
    def page_number(self):
        return self._page_number

    @property
    def left(self):
        return self._left

    @property
    def top(self):
        return self._top

    @property
    def right(self):
        return self._right

    @property
    def bottom(self):
        return self._bottom

    def sort_key(self):
        """Return a comparable value that supports > and < operators."""
        return [self.page_number, self.left, self.top, self.right, self.bottom]

    def width(self):
        return self.right - self.left

    def height(self):
        return self.bottom - self.top

    def merge(self, other):
        """Returns a region set that contains both this and the other region or None if
        the regions are on different pages and therefore not able to merge"""
        if self.page_number != other.page_number:
            return None
        else:
            return Region(self.page_number, min(self.left, other.left),
                          min(self.top, other.top), max(self.right, other.right),
                          max(self.bottom, other.bottom))

    def overlaps_vertically(self, other):
        """Returns true if regions overlap along vertical axis"""
        if self.page_number != other.page_number:
            return False
        else:
            min_bottom = min(self.bottom, other.bottom)
            max_top = max(self.top, other.top)
            return (min_bottom - max_top) >= 0

    def overlaps_horizontally(self, other):
        """Returns true if regions overlap along horizontal axis"""
        if self.page_number != other.page_number:
            return False
        else:
            min_right = min(self.right, other.right)
            max_left = max(self.left, other.left)
            return (min_right - max_left) >= 0

    def is_to_left_of(self, other):
        """Returns true if this region is to left of other region"""
        if self.page_number != other.page_number:
            return False
        else:
            return self.right < other.left

    def is_to_right_of(self, other):
        """Returns true if this region is to right of other region"""
        if self.page_number != other.page_number:
            return False
        else:
            return self.left > other.right

    def is_above(self, other):
        """Returns true if this region is above other region"""
        if self.page_number != other.page_number:
            return False
        else:
            return self.bottom > other.top

    def is_below(self, other):
        """Returns true if this region is below other region"""
        if self.page_number != other.page_number:
            return False
        else:
            return self.top < other.bottom

    def contains(self, other):
        """Returns true if this region contains other region"""
        return (self.page_number == other.page_number and
                self.top <= other.top and
                self.bottom >= other.bottom and
                self.left <= other.left and
                self.right >= other.right)


class Table:
    """Defines a table consisting of 0 or more rows"""

    def add_row(self, row):
        self._rows.append(row)

    def __init__(self):
        self._rows = []

    @property
    def rows(self):
        return self._rows

    def header_row(self):
        return self._rows[0]

    def detail_rows(self):
        return self._rows[1:]

    def row_count(self):
        """Number of rows"""
        return len(self._rows)


class Row:
    """Defines a row consisting of cells"""

    def __init__(self):
        self._cells = []

    def add_cell(self, cell):
        self._cells.append(cell)

    @property
    def cells(self):
        return self._cells

    def cell_count(self):
        """Return number of cells in the row"""
        return len(self._cells)

    def merge(self, other):
        """Return new row that merges this row with other row"""
        if self.cell_count() != other.cell_count:
            raise RuntimeError("Cannot merge rows of different length")
        else:
            merged_row = Row()
            for cell, other_cell in zip(self.cells, other.cells):
                merged_row.add_cell(cell.merge(other_cell))
            return merged_row

    def joined_text(self, join_by=" ", cell_join_by=" "):
        """Return row text joined by optional string argument joined_by"""
        substrings = []
        for cell in self._cells:
            substrings.append(cell.joined_text(cell_join_by))
        return join_by.join(substrings)

    def hexdigest(self):
        """Returns a SHA-256 hex digest of the row text obtained by joining cells"""
        return hashlib.sha256(self.joined_text().encode('utf-8')).hexdigest()

    def select_text(self, regex):
        """Return any text that matches regex"""
        selected = []
        for cell in self._cells:
            selected = selected + cell.select_text(regex)
        return selected

    def matches_text(self, regex):
        """Return true if we have this text"""
        for cell in self._cells:
            if cell.matches_text(regex):
                return True
        return False

    def is_like(self, other):
        """Returns true if the other row has same cell length and digest"""
        return (self.cell_count() == other.cell_count() and
                self.hexdigest() == other.hex_digest())


class Cell:
    """Defines a cell consisting of 0 or more lines of text"""

    def __init__(self, number):
        self._text = []
        self._number = number
        self._width = 0
        self._height = 0
        self._region = None

    @property
    def number(self):
        return self._number

    @property
    def width(self):
        return self._width

    @width.setter
    def width(self, width):
        self._width = width

    @property
    def height(self):
        return self._height

    @height.setter
    def height(self, height):
        self._height = height

    def add_text(self, text):
        self._text.append(text)

    @property
    def text(self):
        return self._text

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region

    def merge(self, other):
        """Return a new cell that is merged with the other"""
        merged_cell = Cell(self.page)
        merged_cell.text = merged_cell.text + other.text

        merged_region = self.region.merge(other.region)
        if merged_region is None:
            merged_region = self.region
        merged_cell.region = merged_region
        merged_cell.width = merged_region.width()
        merged_cell.height = merged_region.height()

        return merged_cell

    def joined_text(self, join_by=" "):
        """Return text joined by optional string argument joined_by"""
        return join_by.join(self._text)

    def hexdigest(self):
        """Returns a SHA-256 hex digest of the cell text"""
        return hashlib.sha256(self.joined_text().encode('utf-8')).hexdigest()

    def select_text(self, regex):
        """Return any text that matches regex"""
        selected = []
        for text in self._text:
            if re.match(regex, text):
                selected.append(text)
        return selected

    def matches_text(self, regex):
        """Return true if we have this text"""
        for text in self._text:
            if re.match(regex, text):
                return True
        return False

    def is_like(self, other):
        """Returns true if the other cell has same digest"""
        return self.hexdigest() == other.hex_digest()


class TextBlock:
    """Defines an area outside a table with one or more lines of text"""

    def __init__(self):
        self._text = []
        self._region = None

    def add_text(self, text):
        self._text.append(text)

    @property
    def text(self):
        return self._text

    def joined_text(self, join_by=" "):
        """Return text joined by optional string argument joined_by"""
        return join_by.join(self._text)

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region

    def select_text(self, regex):
        """Return any text that matches regex"""
        selected = []
        for text in self._text:
            if re.match(regex, text):
                selected.append(text)
        return selected

    def matches(self, regex):
        """Return true if we have this text"""
        for text in self._text:
            if re.match(regex, text):
                return True
        return False

    def joined_text(self, join_by=" "):
        """Return text joined by optional string argument joined_by"""
        return join_by.join(self._text)


class Line:
    """Defines a line of text"""

    def __init__(self, text=None, region=None):
        self._text = text
        self._region = region

    @property
    def text(self):
        return self._text

    @text.setter
    def text(self, text):
        self._text = text

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region


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
