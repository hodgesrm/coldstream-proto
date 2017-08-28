# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Base classes for invoice processing"""

import os
import configparser
import hashlib
import shutil
import unittest

class BaseError(Exception):
    """Denotes a generic error"""

    def __init__(self, msg): 
        Exception.__init__(self, msg)

class Repo:
    """Invoice repository configuration"""
    def __init__(self, file, cfg=None):
        # Ensure config is provided
        if cfg: 
            self._cfg = cfg
        elif file:
            if not os.path.exists(file):
                raise Exception(
                    "ERROR: repo definition file missing or unreadable: {0}"
                    .format(file))
            self._cfg = configparser.ConfigParser()
            successfully_read = self._cfg.read(file)
            if len(successfully_read) == 0:
                raise Exception(
                    "Unable to read CS repo file: {0}".format(file))
        else:
            raise BaseError("Must provide a file path or config instance")

    def create_repo(repo_cfg):
        """Class method to create a repository"""
        root = os.path.dirname(repo_cfg)
        docs = root + "/documents"
        meta = root + "/metadata"
        ocr = root + "/ocr/cache"

        # Don't create a repo twice. 
        if os.path.exists(repo_cfg):
            raise BaseError("Repo already exists: {0}".format(root))

        if not os.path.exists(root):
            os.makedirs(root)
        if not os.path.exists(docs):
            os.makedirs(docs)
        if not os.path.exists(meta):
            os.makedirs(meta)
        if not os.path.exists(ocr):
            os.makedirs(ocr)
        with open(repo_cfg, "w") as f:
            f.write("[documents]\n")
            f.write("dir = {0}\n".format(docs))
            f.write("[metadata]\n")
            f.write("dir = {0}\n".format(meta))
            f.write("[ocr]\n")
            f.write("cache_dir = {0}\n".format(ocr))

        return repo_cfg

    def delete_repo(repo_cfg, force=False):
        """Class method to remove a repository"""
        if not os.path.exists(repo_cfg):
            if force == True:
                return
            else:
                raise BaseError(
                    "Cannot find repo definition: {0}".format(root_cfg))

        # Read the config, then delete local directories + the
        # repo config itself. 
        repo = Repo(repo_cfg)
        Repo._delete_dir(repo, "documents", "dir")
        Repo._delete_dir(repo, "metadata", "dir")
        Repo._delete_dir(repo, "ocr", "cache_dir")
        os.remove(repo_cfg)

    def _delete_dir(repo, section, key):
        """Deletes directory handling error conditions"""
        try:
            dir = repo.get_cfg_value(section, key)
            if os.path.exists(dir):
                shutil.rmtree(dir, True)
        except(configparser.NoSectionError):
            pass

    def get_cfg_value(self, section, key):
        return self._cfg.get(section, key)

    def get_cfg(self):
        """Return the repo configuration"""
        return self._cfg

    def validate(self):
        """Checks the repo configuration"""
        ok = True
        messages = []
        ok &= self._directory_check(messages, "documents", "dir")
        ok &= self._directory_check(messages, "metadata", "dir")
        ok &= self._directory_check(messages, "ocr", "cache_dir")
        return ok, messages

    def _directory_check(self, messages, section, key):
        """Ensure directory is set and exists"""
        dir = self.get_cfg_value(section, key)
        if dir == None:
            messages.append(self._validation_message(section, key, "Value is missing"))
            return False
        elif not os.path.exists(dir):
            messages.append(self._validation_message(section, key, "Value is missing"))
            return False
        else: 
            messages.append(self._validation_message(section, key, "OK"))
            return True 

    def _validation_message(self, section, key, outcome):
        return "Section: {0}  Key: {1}  Result: {2}".format(section, 
            key, outcome) 

class RepoTest(unittest.TestCase):
    def setUp(self):
        self._REPO_ROOT = "testdata"

    def tearDown(self):
        pass

    def test_repo_create(self):
        my_repo = self._REPO_ROOT + "/test_repo_create/repo.cfg"
        repo_cfg = Repo.create_repo(my_repo)
        Repo.delete_repo(repo_cfg)

    def test_load_and_validate(self):
        my_repo = self._REPO_ROOT + "/test_load_and_validate/repo.cfg"
        repo_cfg = Repo.create_repo(my_repo)
        repo = Repo(repo_cfg)
        repo.validate()
        document_dir = repo.get_cfg_value("documents", "dir")
        self.assertIsNotNone(document_dir)
        metadata_dir = repo.get_cfg_value("metadata", "dir")
        self.assertIsNotNone(metadata_dir)
        Repo.delete_repo(repo_cfg)

# Utility methods. 
def sha256(path):
    """Computes SHA-256 hash on file"""
    sha256 = hashlib.sha256()
    with open(path, 'rb') as f:
        for block in iter(lambda: f.read(1024), b''):
            sha256.update(block)
    return sha256.hexdigest()


if __name__ == '__main__':
    unittest.main()
