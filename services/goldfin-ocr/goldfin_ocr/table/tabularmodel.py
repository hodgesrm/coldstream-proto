# Copyright (c) 2017 Goldfin Systems LLC.  All rights reserved. 

"""Model of tabular documents"""

import hashlib
import logging
import re

# Define logger
logger = logging.getLogger(__name__)


class TabularModel:
    """Root of a page-oriented table model"""

    def __init__(self):
        self._pages = {}

    @property
    def children(self):
        return self.pages

    def add_page(self, page):
        self._pages[page.page_number] = page

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
        """Return all blocks that match the predicate or all blocks if
        predicate is None
        """
        blocks = []
        for page in self.pages:
            blocks += page.select_blocks(predicate)
        return blocks

    def select_tables(self, predicate=None):
        """Return all tables that match the predicate or all tables if
        predicate is None
        """
        tables = []
        for page in self.pages:
            tables += page.select_tables(predicate)
        return tables


class Page:
    """Denotes a page in the document, which may contain one or more tables"""

    def __init__(self, number):
        self._page_number = number
        self._tables = []
        self._blocks = []

    @property
    def children(self):
        return self.blocks + self.tables

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

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
    """Defines an area within a scanned document page using
    pixel coordinates.  Regions can be thought of as a set for pixels"""

    def __init__(self, left=0, top=0, right=99999, bottom=99999):
        """Initializes the region

        Any argument that is not set defaults to the corresponding edge. 
        For example, omitting left means the region extends to the left edge.
        """
        self._left = left
        self._top = top
        self._right = right
        self._bottom = bottom

    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)

    def __repr__(self):
        return self.__str__()

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
        return [self.left, self.top, self.right, self.bottom]

    def width(self):
        return self.right - self.left

    def height(self):
        return self.bottom - self.top

    def area(self):
        return self.width() * self.height()

    def merge(self, other):
        """Returns a region set that contains both this and the other region or None if
        the regions are on different pages and therefore not able to merge"""
        return Region(left=min(self.left, other.left),
                          top=min(self.top, other.top),
                          right=max(self.right, other.right),
                          bottom=max(self.bottom, other.bottom))

    def intersect(self, other):
        """Returns returns a region that is the intersection of this and the
        other region or None if regions do not intersect"""
        left = max(self.left, other.left)
        top = max(self.top, other.top)
        right = min(self.right, other.right)
        bottom = min(self.bottom, other.bottom)
        if left < right and top < bottom:
            return Region(left=left, top=top, right=right, bottom=bottom)
        else:
            return None

    def overlaps_vertically(self, other):
        """Returns true if regions overlap along vertical axis"""
        min_bottom = min(self.bottom, other.bottom)
        max_top = max(self.top, other.top)
        return (min_bottom - max_top) >= 0

    def overlaps_horizontally(self, other):
        """Returns true if regions overlap along horizontal axis"""
        min_right = min(self.right, other.right)
        max_left = max(self.left, other.left)
        return (min_right - max_left) >= 0

    def is_to_left_of(self, other):
        """Returns true if this region is to left of other region"""
        return self.right <= other.left

    def is_to_right_of(self, other):
        """Returns true if this region is to right of other region"""
        return self.left >= other.right

    def is_above(self, other):
        """Returns true if this region is above other region"""
        return self.bottom >= other.top

    def is_below(self, other):
        """Returns true if this region is below other region"""
        return self.top <= other.bottom

    def contains(self, other):
        """Returns true if this region contains other region"""
        return (self.top <= other.top and
                self.bottom >= other.bottom and
                self.left <= other.left and
                self.right >= other.right)

    def intersects(self, other):
        """Returns true if this region intersects other region"""
        return self.intersect(other) is not None

    def intersects_vertical_edge(self, x):
        """Returns true if this region intersects a vertical edge give by x
        coordinate"""
        return self.left <= x <= self.right

    def intersects_horizontal_edge(self, y):
        """Returns true if this region intersects a horizontal edge give by y
        coordinate"""
        return self.top <= y <= self.bottom

class Table:
    """Defines a table consisting of 0 or more rows"""

    def __init__(self, page_number=None):
        self._page_number = page_number
        self._rows = []

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

    @property
    def children(self):
        return self.rows

    def add_row(self, row):
        self._rows.append(row)

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

    def __init__(self, page_number=None):
        self._page_number = page_number
        self._cells = []

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

    @property
    def children(self):
        return self.cells

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

    def __init__(self, number, page_number=None):
        self._lines = []
        self._number = number
        self._width = 0
        self._height = 0
        self._page_number = page_number
        self._region = None

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

    @property
    def children(self):
        return self.lines

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

    def add_line(self, line):
        self._lines.append(line)

    @property
    def lines(self):
        return self._lines

    @lines.setter
    def lines(self, lines):
        self._lines = lines

    @property
    def text(self):
        l = []
        for line in self._lines:
            l.append(line.text)
        return l

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region

    def merge(self, other):
        """Return a new cell that is merged with the other"""
        merged_cell = Cell()
        merged_cell.page_number = self.page_number
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
        return join_by.join(self.text)

    def hexdigest(self):
        """Returns a SHA-256 hex digest of the cell text"""
        return hashlib.sha256(self.joined_text().encode('utf-8')).hexdigest()

    def select_text(self, regex):
        """Return any text that matches regex"""
        selected = []
        for text in self.text:
            if re.match(regex, text):
                selected.append(text)
        return selected

    def matches_text(self, regex):
        """Return true if we have this text"""
        for text in self.text:
            if re.match(regex, text):
                return True
        return False

    def is_like(self, other):
        """Returns true if the other cell has same digest"""
        return self.hexdigest() == other.hex_digest()


class TextBlock:
    """Defines an area outside a table with one or more lines of text"""

    def __init__(self, page_number=None):
        self._page_number = page_number
        self._lines = []
        self._region = None

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

    @property
    def children(self):
        return self.lines

    def add_line(self, line):
        self._lines.append(line)

    @property
    def lines(self):
        return self._lines

    @property
    def text(self):
        l = []
        for line in self._lines:
            l.append(line.text)
        return l

    def joined_text(self, join_by=" "):
        """Return text joined by optional string argument joined_by"""
        return join_by.join(self.text)

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region

    def select_text(self, regex):
        """Return any text that matches regex"""
        selected = []
        for text in self.text:
            if re.match(regex, text):
                selected.append(text)
        return selected

    def matches(self, regex):
        """Return true if we have this text"""
        for text in self.text:
            if re.match(regex, text):
                return True
        return False

    def joined_text(self, join_by=" "):
        """Return text joined by optional string argument joined_by"""
        return join_by.join(self.text)


class Line:
    """Defines a line of text with a location"""

    def __init__(self, text=None, region=None, page_number=None):
        self._page_number = page_number
        self._text = text
        self._region = region

    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)

    @property
    def page_number(self):
        return self._page_number

    @page_number.setter
    def page_number(self, page_number):
        self._page_number = page_number

    @property
    def text(self):
        return self._text

    @text.setter
    def text(self, text):
        self._text = text

    def append_text(self, text_fragment):
        if self._text:
            self._text += text_fragment
        else:
            self._text = text_fragment

    @property
    def region(self):
        return self._region

    @region.setter
    def region(self, region):
        self._region = region

    def matches(self, regex):
        """Return true if we have this text"""
        return re.match(regex, self.text)
