#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import weakref
import json

class A:
    def __init__(self, name, parent):
        self._name = name
        if parent is None:
            self._parent = None
        else:
            self._parent = weakref.ref(parent);
            self._parent().add(self)
        self._children = []

    @property
    def name(self):
        return self._name

    @property
    def parent(self):
        return self._parent()

    @property
    def children(self):
        return self._children

    def add(self, child):
        self._children.append(child)

def dump_to_json(obj, indent=2, sort_keys=True):
    """Dumps a object to JSON by supplying default to
    convert objects to dictionaries.
    """
    def converter_fn(unserializable_obj): 
        if callable(unserializable_obj):
            return None
        else:
            return unserializable_obj.__dict__

    return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)

if __name__ == '__main__':
    a1 = A('a1', None)
    a2 = A('a2', a1)
    a3 = A('a3', a1)
    a4 = A('a4', a2)

    print(dump_to_json(a1))
