# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Leaseweb inventory analyzer"""

import datetime
import json
import logging
import requests

from goldfin.client.api.models.observation import Observation

# Standard logging initialization.
logger = logging.getLogger(__name__)

VENDOR = "LeaseWeb"

class LeasewebProcessor():
    """Processing class for leaseweb inventory"""
    def __init__(self, api_key=None, api_url=None):
        """Create processor

        :param api_key: API key used to login to LeaseWeb REST API
        :type api_key: str
        :param api_url: Base URL for API server
        :type api_url: str
        """
        if api_key is None:
            message = "No api_key value provided, required to login"
            logger.fatal(message)
            raise Exception(message)
        self._api_key = api_key
        if api_url is None:
            self._api_url = "https://api.leaseweb.com"
        else:
            self._api_url = api_url

    def execute(self):
        """Observe current inventory and return Observation result

		:return: observation content containing current inventory
        :rtype: Observation
        """
        servers_url = self._api_url + "/bareMetals/v2/servers"
        logger.info("Checking inventory: url={0}".format(servers_url))
        headers = {'X-Lsw-Auth': self._api_key}
        r = requests.get(servers_url, headers=headers)
        if r.status_code != 200:
            message = "Unable to login: {status_code}, {text}".format(
                       status_code=r.status_code, text=r.text)
            logger.fatal(message)
            raise Exception(message)
        r_as_json = r.json()
        servers = r_as_json['servers']
        server_details = []
        for server in servers:
            server_id = server['id']
            server_url = "{0}/{1}".format(servers_url, server_id)
            r2 = requests.get(server_url, headers=headers)
            logger.debug("Server ID: {id}".format(id=server_id))
            logger.debug("Status code: {0}".format(r2.status_code))
            logger.debug("Text: {0}".format(r2.text))
            logger.debug(json.dumps(r2.json(), indent=2))
            server_details.append(r2.json())

        obs = Observation()
        obs.vendor_identifier = VENDOR
        obs.effective_date = datetime.datetime.now()
        obs.observation_type = "HOST_INVENTORY"
        obs.data = json.dumps(server_details, sort_keys=True)
        obs.version = "1.0"

        return obs
