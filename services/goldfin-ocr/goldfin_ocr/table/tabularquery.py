# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Query processing for tabular data"""

import logging
import re
from . import tabularmodel as tm
import goldfin_ocr.data as data

# Define logger
logger = logging.getLogger(__name__)


class QueryEngine:
    """Query engine for tabular models.  Model is loaded once after which 
       the engine can serve any number of queries."""

    def __init__(self, model):
        """Load a tabular model into the engine"""
        self._model = model

    @property
    def model(self):
        return self._model

    def query(self, root=None):
        """Creates a query query"""
        if root is None:
            root = self._model
        return TabularQuery(engine=self, root=root)

    def page_query(self, page_number, root=None):
        """Short hand to get a single page"""
        return self.query(root=root).matches_type(tm.Page).page(
            page_number)

    def line_query(self, root=None):
        """Shorthand to get a query on Lines"""
        return self.query(root=root).matches_type(tm.Line)


# Cut functions for queries.  These control recursion on entities that pass
# predicate filters.
def no_cut(entity):
    """Continue recursion into yielded entity"""
    return False


def cut(entity):
    """Stop recursion on yielded entity"""
    return True


class TabularQuery:
    """Does all the work of handling queries"""
    def __init__(self, engine, root=None):
        """Creates a new query
        :param engine: Query engine
        :type engine: TabularEngine
        :param root: Root query location.  Defaults to top of model.
        """
        self._engine = engine
        self._root = root
        self._cut = no_cut
        self._predicates = []

    # General predicate functions that work on all entities start here.
    def matches_type(self, type):
        def _matches_type(entity):
            return isinstance(entity, type)

        self._predicates.append(_matches_type)
        return self

    def page(self, page_no):
        """Returns true if entity region matches page number"""

        def _matches_page_number(entity):
            return page_no == entity.page_number

        self._predicates.append(_matches_page_number)
        return self

    def matches_regex(self, regex):
        """Returns true if entity text contains the provided regex"""
        def _matches_regex(entity):
            if re.match(regex, entity.text):
                return True
            else:
                return False

        self._predicates.append(_matches_regex)
        return self

    def matches_currency(self):
        """Returns true if entity text contains currency data"""

        def _matches_currency(entity):
            if entity.text is None or data.extract_currency(entity.text) is None:
                return False
            else:
                return True

        self._predicates.append(_matches_currency)
        return self

    def matches_date(self):
        """Returns true if entity text contains a date"""

        def _matches_date(entity):
            return False

        self._predicates.append(_matches_date)
        return self

    # Geometric location predicates start here.
    def intersects(self, region):
        """Returns true if the entity's region intersects the given region"""
        def _intersects(entity):
            return entity.region.intersects(region)

        self._predicates.append(_intersects)
        return self

    def is_to_right_of(self, region):
        """Returns true if the entity lies to the right of the given region
        with intersecting vertical axes"""

        def _is_to_right_of(entity):
            return entity.region.is_to_right_of(region) and entity.region.overlaps_vertically(region)

        self._predicates.append(_is_to_right_of)
        return self

    def is_to_left_of(self, region):
        """Returns true if the entity lies to the left of the given region
        with intersecting vertical axes"""

        def _is_to_left_of(entity):
            return entity.region.is_to_left_of(region) and entity.region.overlaps_vertically(region)

        self._predicates.append(_is_to_left_of)
        return self

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