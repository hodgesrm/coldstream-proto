# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Query processing for tabular data"""

import logging
import re
from . import tabularmodel as tm
import goldfin_ocr.data_utils as data_utils

# Define logger
logger = logging.getLogger(__name__)


class QueryEngine:
    """Query engine for tabular models.  Model is loaded once after which 
       the engine can serve any number of queries."""

    def __init__(self, model):
        """Load a tabular model into the engine"""
        self._model = model
        self._build_index()

    def _build_index(self):
        """Builds page index by running query over each page"""
        self._page_index = PageLocationIndex()
        page_query = self.tabular_query().matches_type(
            tm.Page).cut().generate()
        for page in page_query:
            for entity in self.tabular_query(root=page).generate():
                self._page_index.add(entity)

    @property
    def model(self):
        return self._model

    @property
    def page_index(self):
        return self._page_index

    def tabular_query(self, root=None):
        """Creates a query that operates by scanning the document model
        recursively"""
        if root is None:
            root = self._model
        return TabularQuery(root=root)

    def tabular_page_query(self, page_number, root=None):
        """Short hand to get a query to operate on a single page"""
        return self.tabular_query(root=root).matches_type(tm.Page).page(
            page_number)

    def tabular_line_query(self, root=None):
        """Shorthand to get a query on Lines"""
        return self.tabular_query(root=root).matches_type(tm.Line)

    def indexed_query(self):
        """Creates a query that uses a page index to return values"""
        return IndexedQuery(index=self._page_index)


class PageLocationIndex:
    """Implements an index of entities by page and cell where cells are
    defined by a region and point to all entities that overlap the region.
    """

    def __init__(self, x_step=200, y_step=100):
        """Construct page index

        :param x_step: Index cell width
        :type x_step: int
        :param y_step: Index cell height
        :type y_step: int
        """
        self._x_step = x_step
        self._y_step = y_step
        self._page_nodes = {}

    def pages(self):
        return sorted(self._page_nodes.keys())

    def add(self, entity):
        """Add provided entity to the index, adding a page node if required.

        :param entity: An entity to add.  Must be a page or lower entity
        """
        page_number = entity.page_number
        if page_number is not None:
            if self._page_nodes.get(page_number) is None:
                # Add new page node.
                self._page_nodes[page_number] = PageNode(page_number,
                                                         self._x_step,
                                                         self._y_step)
            page_node = self._page_nodes.get(page_number)
            page_node.add(entity)

    def page_left_right_accessor(self, page_number, left, right):
        if page_number is None or self._page_nodes.get(page_number):
            yield from self._page_nodes[page_number].generate_x_range(left,
                                                                      right)

    def page_top_bottom_accessor(self, page_number, top, bottom):
        if page_number is None or self._page_nodes.get(page_number):
            yield from self._page_nodes[page_number].generate_y_range(top,
                                                                      bottom)


class PageNode:
    """Indexes entity edges within a single page

    Edge indexes work using a 'grid' approach.  Imagine that the page is
    divided into a grid of rectangles say 100 pixels high and 200 wide.
    The indexes track which entities fall into which rectangle, so that
    when we run queries we first look for the rectangles that intersect
    with our desired entity location.  This cuts down the query surface
    considerably.

    Since searches mostly look at full rows or columns it's enough to
    track only the row or the column rather than all individual rectangles.
    We do this with the _x_cells and _y_cells arrays.
    """

    def __init__(self, page, x_step, y_step):
        self._page = page
        self._x_step = x_step
        self._y_step = y_step
        self._x_cells = []
        self._y_cells = []

    def add(self, entity):
        """"Add an entity to the index."""
        if hasattr(entity, 'region') and entity.region is not None:
            self._add_to_x_cells(entity)
            self._add_to_y_cells(entity)

    def _add_to_x_cells(self, entity):
        """Add the entity to any column of rectangles it intersects."""
        start_x_index = entity.region.left // self._x_step
        end_x_index = entity.region.right // self._x_step
        for x_index in range(start_x_index, end_x_index + 1):
            self._add_entity_to_cell(self._x_cells, x_index, entity)

    def _add_to_y_cells(self, entity):
        """Add the entity to any row of rectangles it intersects."""
        start_y_index = entity.region.top // self._y_step
        end_y_index = entity.region.bottom // self._y_step
        for y_index in range(start_y_index, end_y_index + 1):
            self._add_entity_to_cell(self._y_cells, y_index, entity)

    def _add_entity_to_cell(self, cells, index, entity):
        """Takes care of adding to array, extending if necessary."""
        while index > len(cells) - 1:
            cells.append(set())
        cells[index].add(entity)

    def generate_x_range(self, start, end):
        """Generate entities in grid columns overlapping given start/end."""
        yield from self._generate_range(self._x_cells, start, end,
                                        self._x_step)

    def generate_y_range(self, start, end):
        """Generate entities in grid rows overlapping given start/end."""
        yield from self._generate_range(self._y_cells, start, end,
                                        self._y_step)

    def _generate_range(self, cells, start, end, step):
        found = set()
        # Collect set of entities in range.
        start_coord_index = start // step
        end_coord_index = end // step
        for coord_index in range(start_coord_index, end_coord_index + 1):
            if coord_index > len(cells) - 1:
                break
            else:
                found |= cells[coord_index]

        # Generate from found entities.
        for entity in found:
            yield entity


# Cut functions for queries.  These control recursion on entities that pass
# predicate filters.
def no_cut(entity):
    """Continue recursion into yielded entity"""
    return False


def cut(entity):
    """Stop recursion on yielded entity"""
    return True


class AbstractQuery:
    """Implements shared logic common to all query types"""

    def __init__(self):
        """Creates a new query
        """
        self._predicates = []

    # Add a general predicate.  You can put in any logic that pleases you.
    def predicate(self, predicate):
        """Adds a predicate to the query. Predicates are applied in FIFO order
        so it's better to put cheap operations in first so expensive ones can
        run on a smaller set.

        :param predicate: A function of the form (entity) -> Boolean
        :type predicate: function
        """
        self._predicates.append(predicate)
        return self

    # General predicate functions that work on all entities start here.
    def matches_type(self, type):
        def _matches_type(entity):
            return isinstance(entity, type)

        self.predicate(_matches_type)
        return self

    def page(self, page_no):
        """Returns true if entity region matches page number"""

        def _matches_page_number(entity):
            return page_no == entity.page_number

        self.predicate(_matches_page_number)
        return self

    def matches_regex(self, regex):
        """Returns true if entity text contains the provided regex"""

        def _matches_regex(entity):
            if re.match(regex, entity.text):
                return True
            else:
                return False

        self.predicate(_matches_regex)
        return self

    def matches_currency(self):
        """Returns true if entity text contains currency data"""

        def _matches_currency(entity):
            if entity.text is None or data_utils.extract_currency(
                    entity.text) is None:
                return False
            else:
                return True

        self.predicate(_matches_currency)
        return self

    def matches_date(self):
        """Returns true if entity text contains a date"""

        def _matches_date(entity):
            return False

        self.predicate(_matches_date)
        return self

    # Geometric location predicates start here.
    def contains(self, region):
        """Returns true if the entity's region contains the given region"""

        def _contains(entity):
            return entity.region.contains(region)

        self.predicate(_contains)
        return self

    def intersects(self, region):
        """Returns true if the entity's region intersects the given region"""

        def _intersects(entity):
            return entity.region.intersects(region)

        self.predicate(_intersects)
        return self

    def intersects_vertical_edge(self, x):
        """Returns true if the entity's region intersects a vertical edge"""

        def _intersects_vertical_edge(entity):
            return entity.region.intersects_vertical_edge(x)

        self.predicate(_intersects_vertical_edge)
        return self

    def intersects_horizontal_edge(self, y):
        """Returns true if the entity's region intersects a horizontal edge"""

        def _intersects_horizontal_edge(entity):
            return entity.region.intersects_horizontal_edge(y)

        self.predicate(_intersects_horizontal_edge)
        return self

    def is_to_right_of(self, region):
        """Returns true if the entity lies to the right of the given region
        with intersecting vertical axes"""

        def _is_to_right_of(entity):
            return entity.region.is_to_right_of(
                region) and entity.region.overlaps_vertically(region)

        self.predicate(_is_to_right_of)
        return self

    def is_to_left_of(self, region):
        """Returns true if the entity lies to the left of the given region
        with intersecting vertical axes"""

        def _is_to_left_of(entity):
            return entity.region.is_to_left_of(
                region) and entity.region.overlaps_vertically(region)

        self.predicate(_is_to_left_of)
        return self

    # Query execution functions start here.
    def generate(self):
        """Returns a generator that iterates over query results qualified
        by predicates"""
        pass

    def first(self):
        """Returns the first item in the generated list or none"""
        for entity in self.generate():
            return entity
        return None

    def count(self):
        """Returns number of entities found, just like SQL count"""
        count = 0
        for entity in self.generate():
            count += 1
        return count

    def order_by_left_edge(self):
        """Returns entities in increasing left edge order"""
        return sorted(self.generate(), key=lambda entity: entity.region.left)

    def order_by_top_edge(self):
        """Returns entities in increasing left edge order"""
        return sorted(self.generate(), key=lambda entity: entity.region.top)


class TabularQuery(AbstractQuery):
    """Implements a query that uses recursion over the tabular model to
    access entities"""

    def __init__(self, root=None):
        """Creates a new query
        :param root: Root query location.  Defaults to top of model.
        """
        super().__init__()
        self._root = root
        self._cut = no_cut

    # Cut functions to control recursion start here.
    def cut(self):
        """Terminate deeper recursion on when matching entity is found"""
        self._cut = cut
        return self

    # Query execution functions start here.
    def generate(self):
        """Iterates depth-first over the document model generating entities as
        they are found in page order"""

        def _recursively_generate(entity):
            # See if entity can pass predicates.
            passed = True
            for predicate in self._predicates:
                if predicate(entity) is False:
                    passed = False
                    break

            # Yield entity that passes predicates and terminate recursion on
            # cut.
            if passed:
                yield entity
                if self._cut(entity):
                    return

            # Descend into children if they exist and there's no cut
            if hasattr(entity, 'children'):
                for child in entity.children:
                    yield from _recursively_generate(child)

        for entity in _recursively_generate(self._root):
            yield entity


class IndexedQuery(AbstractQuery):
    """Implements a query that uses recursion over the tabular model to
    access entities"""

    def __init__(self, index):
        """Creates a new query
        :param index: Index used by query
        :type index: PageLocationIndex
        """
        super().__init__()
        self._index = index
        self._page = None
        self._range = None

    # Predicate implementations that are specific to index queries.
    def page(self, page_no):
        """Selects entities that match a specific page"""
        self._page = page_no
        return self

    def x_range(self, start, end):
        """Selects entities whose left value falls in the start-end range"""
        self._range = tm.Region(left=start, top=None, right=end, bottom=None)
        return self

    def y_range(self, start, end):
        """Selects entities whose top value falls in the start-end range"""
        self._range = tm.Region(left=None, top=start, right=None, bottom=end)
        return self

    # Query execution functions start here.
    def generate(self):
        """Iterates over values read from the index"""

        # Select pages over which to iterate.
        if self._page is None:
            pages = self._index.pages()
        else:
            pages = [self._page]

        # Start iteration.
        for page_number in pages:
            # Select iterator.
            if self._range is None:
                logger.debug("STARTING: NO RANGE")
                accessor = self._index.page_left_right_accessor(page_number, 0,
                                                                99999)
            elif self._range.left is not None:
                logger.debug("STARTING: left-right range")
                accessor = self._index.page_left_right_accessor(page_number,
                                                                self._range.left,
                                                                self._range.right)
            elif self._range.top is not None:
                logger.info("STARTING: top-bottom range")
                accessor = self._index.page_top_bottom_accessor(page_number,
                                                                self._range.top,
                                                                self._range.bottom)
            else:
                logger.info("STARTING: default")
                accessor = self._index.page_left_right_accessor(page_number, 0,
                                                                99999)

            for entity in accessor:
                # See if entity can pass predicates.
                logger.debug("Candidate: {0}".format(entity))
                passed = True
                for predicate in self._predicates:
                    if predicate(entity) is False:
                        passed = False
                        break

                # Yield entity that passes predicates
                if passed:
                    yield entity
