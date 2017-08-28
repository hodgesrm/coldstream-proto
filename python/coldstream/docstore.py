# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import configparser
import hashlib
import json
import os
import shutil
import tempfile
import unittest
import uuid

import ip_base

class DocumentStore:
    """Stores and retrieves files"""
    def __init__(self, repo):
        """Initializes with repo settings"""
        self._repo = repo
        self._doc_dir = repo.get_cfg_value("documents", "dir")

    def put(self, source_path, metadata):
        """Atomically store file and return location tag.

        :param source_path: A string containing the path name to the document.
        :param metadata: A dictionary of user-define key/value tags 
            associated with the document.
        """
        id = str(uuid.uuid4())
        storage_path, storage_meta_path = self._make_storage_path(id)
        shutil.copyfile(source_path, storage_path)
        sha256 = self._sha256(source_path)
        metadata['sha256'] = sha256
        metadata['id'] = id
        with open(storage_meta_path, "w") as meta_fp:
            json.dump(metadata, meta_fp, indent=2, sort_keys=True)
        return id, sha256

    def get(self, id, target_path):
        """Retrieve the document to a target path and return metadata"""
        storage_path, storage_meta_path = self._make_storage_path(id, 
            exists_ok=True)
        metadata = self._get_metadata(storage_meta_path)
        shutil.copyfile(storage_path, target_path)
        return metadata

    def get_storage_path(self, id):
        """Return the document storage path"""
        storage_path, ignore = self._make_storage_path(id, exists_ok=True)
        return storage_path

    def get_metadata(self, id):
        """Retrieve dictionary of document metadata tags."""
        ignore, storage_meta_path = self._make_storage_path(id, 
            exists_ok = True)
        return self._get_metadata(storage_meta_path)

    def validate(self, id):
        """Validates that a file with id exists and matches its SHA-256 
        hash, returning true if successful otherwise raising an exception"""
        storage_path, storage_meta_path = self._make_storage_path(id, 
            exists_ok=True)
        metadata = self._get_metadata(storage_meta_path)
        if not os.path.exists(storage_path):
            raise ip_base.BaseError(
                "Storage path not found: {0}".format(storage_path))
        sha256 = self._sha256(storage_path)
        if not sha256 == metadata['sha256']:
            raise ip_base.BaseError(
                "Stored file does not match stored SHA-256 hash: {0} stored={1} actual={2}".format(
                     storage_path, metadata['sha256'], sha256))
        return True

    def list(self):
        """Return a list of document IDs"""
        raise Error("Not implemented")

    def delete(self, id):
        """Deletes document corresponding to id"""
        storage_path, storage_meta_path = self._make_storage_path(id, 
            exists_ok=True)
        self._remove(storage_path)
        self._remove(storage_meta_path)

    def _remove(self, path):
        """Remove a file ignoring errors"""
        try:
            os.remove(path)
        except(FileNotFoundError):
            pass

    def _make_storage_path(self, id, exists_ok=False):
        """Returns paths for stored file and accompanying metadata"""
        storage_path = self._doc_dir + "/" + id
        storage_meta_path = storage_path + "." + "meta"
        if os.path.exists(storage_path) and exists_ok == False:
            raise ip_base.BaseError("Path exists: {0}".format(storage_path))
        if os.path.exists(storage_meta_path) and exists_ok == False:
            raise ip_base.BaseError("Path exists: {0}".format(storage_meta_path))
        return storage_path, storage_meta_path

    def _get_metadata(self, storage_meta_path):
        with open(storage_meta_path, "r") as meta_fp:
            return json.load(meta_fp)

    def _sha256(self, path):
        """Computes SHA-256 hash on file"""
        sha256 = hashlib.sha256()
        with open(path, 'rb') as f:
            for block in iter(lambda: f.read(1024), b''):
                sha256.update(block)
        return sha256.hexdigest()

class DocumentStoreTest(unittest.TestCase):
    def setUp(self):
        self._REPO_ROOT = "testdata/docstore_testing"
        self._REPO_TMP = "testdata/docstore_testing_tmp"

    def tearDown(self):
        pass

    def test_putget(self):
        """Validate that we can add a file and get it back again"""
        repo = self.util_get_repo()
        input = self.util_make_file("test_putget.input", "Some data")
        output = self.util_make_filename("test_putget.output", delete=True)

        doc_store = DocumentStore(repo)
        tags = {}
        tags["a"] = "A"
        tags["b"] = "B"
        id,sha256 = doc_store.put(input, tags)
        self.assertIsNotNone(id)
        self.assertIsNotNone(sha256)
        self.assertTrue(doc_store.validate(id))
        tags2 = doc_store.get(id, output)
        self.assertIsNotNone(tags2)
        self.assertEqual(tags["a"], tags2["a"])
        self.assertEqual(tags["b"], tags2["b"])
        self.assertEqual(id, tags["id"])
        self.assertEqual(sha256, tags["sha256"])
        self.assertTrue(os.path.exists(output))
        self.assertEqual(os.path.getsize(input), os.path.getsize(output))

    def util_make_filename(self, name, delete=False):
        fname = self._REPO_TMP + "/" + name
        if delete == True and os.path.exists(fname):
            os.remove(fname)
        return fname
      
    def util_make_file(self, name, contents=None):
        """Creates a file and adds some data"""
        # Don't use tempfile utilities as we need a visible file that
        # sticks around.
        fname = self.util_make_filename(name)
        os.makedirs(os.path.dirname(fname), exist_ok=True)
        with open(fname, "w") as fp:
            fp.seek(0)
            if contents != None:
                fp.write(contents)
        return fname

    def util_get_repo(self):
        repo_cfg = self._REPO_ROOT + "/repo.cfg"
        ip_base.Repo.delete_repo(repo_cfg, force=True)
        ip_base.Repo.create_repo(repo_cfg)
        return ip_base.Repo(repo_cfg)

if __name__ == '__main__':
    unittest.main()
