import uuid
import json
import unittest
from flask import current_app

import storage_daemon as daemon


class FlaskrTestCase(unittest.TestCase):
    def setUp(self):
        daemon.app.config['TESTING'] = True
        print(daemon.app.config)
        self.app = daemon.app.test_client()
        with daemon.app.app_context():
            print(current_app.name)

    def tearDown(self):
        print("Done!")

    def test_tenant_life_cycle(self):
        my_uuid=str(uuid.uuid4);
        rv = self._create_tenant(my_uuid);
        print(rv.data)
        assert rv.status_code == 200
        assert b'tenantId' in rv.data

        rv2 = self._get_tenant(my_uuid);
        assert rv2.status_code == 200
        assert b'tenantId' in rv2.data

        rv3 = self._delete_tenant(my_uuid)
        assert rv3.status_code == 200

    def test_tenant_file_load(self):
        pass

    def _create_tenant(self, my_uuid):
        tenant_id = my_uuid
        tenant_name = "tenant-A"
        request_data = dict(tenantId=tenant_id, name=tenant_name)
        print("Posting tenant creation")
        return self.app.post('/api/v1/tenant-data',
                             data=json.dumps(request_data), content_type="application/json")

    def _get_tenant(self, my_uuid):
        return self.app.get("/api/v1/tenant-data/{0}".format(my_uuid),content_type="application/json")

    def _delete_tenant(self, my_uuid):
        return self.app.delete("/api/v1/tenant-data/{0}".format(my_uuid),content_type="application/json")


if __name__ == '__main__':
    unittest.main()
