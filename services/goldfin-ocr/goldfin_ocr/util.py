# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import hashlib
import json


def sha256(path):
    """Compute SHA-256 hash on file

    :param path: Path to file
    :type path: str
    :returns str
    """
    sha256 = hashlib.sha256()
    with open(path, 'rb') as f:
        for block in iter(lambda: f.read(1024), b''):
            sha256.update(block)
    return sha256.hexdigest()


def dump_to_json(obj, indent=2, sort_keys=True):
    """Dumps a object to JSON by supplying default to
    convert objects to dictionaries.

    :param obj: Object to serialized to JSON
    :type obj: Object
    :param indent: Indentation spaces
    :type indent: int
    :param sort_keys: Sort keys in order
    :type sort_keys: bool
    """
    converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
    return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)
