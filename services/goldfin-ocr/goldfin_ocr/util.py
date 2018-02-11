# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import hashlib

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
