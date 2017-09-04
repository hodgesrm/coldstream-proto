# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Model of tabular documents"""

import unittest

class TabularModel:
    """Root of a page-oriented table model"""

    def __init__(self):
        self._pages = {}

    def add_page(self, page):
        self._pages[page.number] = page
        page.previous_page = self._pages.get(page.number - 1)
        page.next_page = self._pages.get(page.number + 1)
        if page.previous_page != None:
            page.previous_page.next_page = page
        if page.next_page != None:
            page.next_page.previous_page = page

    def pages(self):
        """Returns array of pages"""
        pages = []
        for page_number in sorted(self._pages):
            pages.append(self._pages[page_number])
        return pages

    def page_count(self):
        return len(self._pages)


class Page:
    """Denotes a page in the document, which may contain one or more tables"""

    def __init__(self, number):
        self._number = number
        self._tables = []
        self._next_page = None
        self._previous_page = None

    @property
    def number(self):
        return self._number

    def add_table(self, table):
        self._tables.append(table)

    @property
    def tables(self):
        return self._tables

    @property
    def next_page(self):
        return self._next_page

    @next_page.setter
    def next_page(self, next_page):
        self._next_page = next_page

    @property
    def previous_page(self):
        return self._previous_page

    @previous_page.setter
    def previous_page(self, previous):
        self._previous_page = previous


class Location:
    """Defines a location within a scanned document using the page number
    and pixel coordinates"""

    def __init__(self, page, left, top, right, bottom):
        self._page = page
        self._left = left
        self._top = top
        self._right = right
        self._bottom = bottom

    @property
    def page(self):
        return self._page

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


class Table:
    """Defines a table consisting of 0 or more rows"""

    def __init__(self):
        self._rows = []

    def add_row(self, row):
        self._rows.append(row)

    @property
    def rows(self):
        return self._rows

    def header_row(self):
        return self._rows[0]

    def row_count(self):
        """Number of rows"""
        return len(self._rows)


class Row:
    """Defines a row consisting of cells"""

    def __init__(self):
        self._cells = []

    @property
    def locator(self):
        return self._locator

    @locator.setter
    def locator(self, locator):
        self._locator = locator

    def add_cell(self, cell):
        self._cells.append(cell)

    @property
    def cells(self):
        return self._cells

    def cell_count(self):
        """Return number of cells in the row"""
        return len(self._cells)


class Cell:
    """Defines a cell consisting of 0 or more lines of text"""

    def __init__(self):
        self._text = []
        self._locator = None

    def add_text(self, text):
        self._text.append(text)

    @property
    def text(self):
        return self._text

    @property
    def locator(self):
        return self._locator

    @locator.setter
    def locator(self, locator):
        self._locator = locator

class TableModelTest(unittest.TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_pages(self):
        """Validate that we can set up a model with pages"""
        model = TabularModel()
        for p in range(1,4):
            page = Page(p)
            self.assertEqual(p, page.number)
            print("Adding: " + str(page.__dict__))
            model.add_page(page)

        # Check page counts.
        pages = model.pages
        self.assertIsNotNone(pages)
        self.assertEqual(3, model.page_count())

        # Check prev/next relationships.
        for page in pages():
            print("Verifying: " + str(page.__dict__))
            prev = page.previous_page
            if (page.number > 1):
                self.assertIsNotNone(prev)
                self.assertEqual(prev.next_page.number, page.number)
            else:
                self.assertIsNone(prev)

            next = page.next_page
            if (page.number < 3):
                self.assertIsNotNone(next)
                self.assertEqual(next.previous_page.number, page.number)
            else:
                self.assertIsNone(next)

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
            cell = Cell()
            cell.add_text('a')
            cell.add_text('b')
            cell.locator = Location(13, (c - 1) * 100, 0, (c * 100) -1, 50)
            row.add_cell(cell)

        self.assertEqual(2, row.cell_count())
        cells = row.cells
        self.assertIsNotNone(cells)
        locator = cells[0].locator
        self.assertIsNotNone(locator)
        self.assertEqual(13, locator.page)

if __name__ == '__main__':
    unittest.main()