# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Public API for invoice processing"""

import jsonpickle
import os
import shutil
import unittest
import uuid

import ip_base

class MetadataStore:
    """Stores and retrieves metadata"""
    def __init__(self, repo):
        """Initializes with repo settings"""
        self._repo = repo
        self._meta_dir = repo.get_cfg_value("metadata", "dir")

    def put(self, metadata, id=None):
        """Store serialized metadata instance

        :param metadata: class instance to store
        """
        if id == None:
            id = str(uuid.uuid4())
        metadata_path = self._make_meta_file_path(id, 
            metadata.__class__.__name__)
        serialized = jsonpickle.encode(metadata)
        with open(metadata_path, "w") as meta_fp:
            meta_fp.write(serialized)
        return id

    def get(self, id, metadata_class):
        """ Retrieve and deserialize a single instance"""
        metadata_path = self._make_meta_file_path(id, metadata_class.__name__)
        with open(metadata_path, "r") as meta_fp:
            serialized = meta_fp.read()
        return jsonpickle.decode(serialized)

    def list(self, metadata_class):
        """Return a deserialized list of instances"""
        id_list = os.listdir(self._make_meta_dir_path(metadata_class.__name__))
        result_list = []
        for id in id_list:
            result_list.append(self.get(id, metadata_class))
        return result_list

    def delete(self, id, metadata_class):
        """Deletes metadata instance corresponding to id"""
        metadata_path = self._make_meta_file_path(id, metadata_class.__name__)
        os.remove(metadata_path)

    def _make_meta_dir_path(self, class_name):
        """Returns path for metadata class directory"""
        storage_base = self._meta_dir + "/" + class_name
        if not os.path.exists(storage_base):
            os.makedirs(storage_base)
        return storage_base

    def _make_meta_file_path(self, id, class_name, exists_ok=True):
        """Returns path for metadata instance"""
        storage_path = self._make_meta_dir_path(class_name) + "/" + id
        if os.path.exists(storage_path) and exists_ok == False:
            raise ip_base.BaseError("Path exists: {0}".format(storage_path))
        return storage_path

class Class1():
    def __init__(self, attr1, attr2):
        self.id = str(uuid.uuid4())
        self.attr1 = attr1
        self.attr2 = Class2(attr2)

class Class2():
    def __init__(self, attr2):
        self.attr2 = attr2

class MetadataStoreTest(unittest.TestCase):
    def setUp(self):
        self._REPO_ROOT = "testdata/metastore_testing"

    def tearDown(self):
        pass

    def test_putget(self):
        """Validate that we can add an object and get it back again"""
        repo = self.util_get_repo("test_putget")
        metadata_store = MetadataStore(repo)

        t1 = Class1("a", "b")
        self.assertEqual("a", t1.attr1)
        self.assertEqual("b", t1.attr2.attr2)
        id1 = metadata_store.put(t1)
        t1_after = metadata_store.get(id1, Class1)
        self.assertIsNotNone(t1_after)
        self.assertEqual(t1.attr1, t1_after.attr1)
        self.assertEqual(t1.attr2.attr2, t1_after.attr2.attr2)

    def test_list(self):
        """Validate that we can add multiple instances and get them back"""
        repo = self.util_get_repo("test_list")
        metadata_store = MetadataStore(repo)

        c1 = Class1("c", "d")
        c2 = Class1("e", "f")
        c3 = Class1("g", "h")
        t1 = metadata_store.put(c1, id=c1.id)
        t2 = metadata_store.put(c2, id=c2.id)
        t3 = metadata_store.put(c3, id=c3.id)
   
        data_list = metadata_store.list(Class1)
        self.assertEqual(3, len(data_list))
        data_list_map = {}
        for t in data_list:
            data_list_map[t.id] = t
        self.assertIsNotNone(data_list_map[t1])
        self.assertIsNotNone(data_list_map[t2])
        self.assertIsNotNone(data_list_map[t3])

    def util_get_repo(self, name):
        repo_cfg = self._REPO_ROOT + "/" + name + "/repo.cfg"
        ip_base.Repo.delete_repo(repo_cfg, force=True)
        ip_base.Repo.create_repo(repo_cfg)
        return ip_base.Repo(repo_cfg)

if __name__ == '__main__':
    unittest.main()
