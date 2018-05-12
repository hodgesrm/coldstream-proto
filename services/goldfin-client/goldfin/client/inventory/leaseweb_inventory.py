# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Leaseweb inventory analyzer"""

import datetime
import json
import logging
import requests

from goldfin.client.api.models.observation_content import ObservationContent

# Standard logging initialization.
logger = logging.getLogger(__name__)

VENDOR = "leaseweb"

# Simplest possible script to read Leaseweb systems. 
class LeasewebProcessor():
    def __init__(self, api_key):
        if api_key is None:
            message = "No api_key value provided, check config file"
            logger.fatal(message)
            raise Exception(message)
        self._api_key = api_key

    def execute(self):
        """Observe current inventory and return ObservationContent result

		:return: observation content containing current inventory
        :rtype: ObservationContent
        """
        servers_url = "https://api.leaseweb.com/bareMetals/v2/servers"
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

        obs = ObservationContent()
        obs.vendor = VENDOR
        obs.effective_date = str(datetime.datetime.now())
        obs.observation_type = "INVENTORY"
        obs.data = json.dumps(server_details, sort_keys=True)

        return obs
