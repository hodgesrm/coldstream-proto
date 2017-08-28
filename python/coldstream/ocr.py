# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import json
import os
import shutil
import unittest

import ip_base

class OcrError(ip_base.BaseError):
    """Denotes a scanning error"""
    def __init__(self, msg):
        ip_base.BaseError.__init__(self, msg)

class Ocr:
    """Submits files to OCR for scanning"""
    def __init__(self, repo):
        """Initializes with repo settings"""
        self._repo = repo
        self._ocr_cache_dir = repo.get_cfg_value("ocr", "cache_dir")

    def scan(self, source_path, output_path):
        """Scan a file returning a document containing the scan result

        :param source_path: The path name of the document to be scanned
        :param output_path: The path name of the scan output document 
        """
        # Compute SHA-256 on file, then check for a file in the cache.
        sha256 = ip_base.sha256(source_path)
        ocr_path = self._make_ocr_path(sha256)
        if os.path.exists(ocr_path):
            shutil.copyfile(source_path, output_path)
            return True

        # Otherwise submit doc for scanning. 
        raise OcrError("Remote scanning not implemented yet")

    def _make_ocr_path(self, sha256):
        """Computes cache path for a scanned file using sha256 hash of 
        said file
        """
        ocr_path = self._ocr_cache_dir + "/" + sha256
        return ocr_path
