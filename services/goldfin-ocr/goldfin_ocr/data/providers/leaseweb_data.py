# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""Leaseweb data analyzer"""

import json
import logging

from goldfin_ocr.json_xlate import SwaggerJsonEncoder
from goldfin_ocr.api.models.host import Host
from goldfin_ocr.api.models.observation import Observation
from goldfin_ocr.api.models.result import Result
import goldfin_ocr.vendors as vendors

# Define logger
logger = logging.getLogger(__name__)


class LeasewebDataProcessor:
    """Leaseweb Data Processor"""

    def get_results(self, observation, data_series_id):
        """Analyze observation and return one or more results

        :param observation: An observation object
        :type observation Observation
        :param data_series_id: Data series UUID
        :type data_series_id: str
        :returns: list of Result
        """

        # Deserialize the observation.
        #print("DATA_SERIES_ID: {0}".format(data_series_id))
        #print("DATA: {0}".format(observation.data))
        server_list = json.loads(observation.data)

        # Iterate over all servers in the observation and create a 
        # Host instance for each one.
        results = []
        for server in server_list:
            #print("SERVER: {0}".format(server))

            # Insert as much as possible into the host record.
            host = Host()
            host.vendor_identifier = vendors.LEASEWEB
            host.effective_date = observation.effective_date
            host.data_series_id = data_series_id
            host.host_type = "BARE_METAL"

            host.host_id = server.get('id')

            contract = server.get('contract')
            if contract:
                host.resource_id = contract.get('internalReference')
                networkTraffic = contract.get('networkTraffic')
                if networkTraffic:
                    raw_limit = int(networkTraffic.get('datatrafficLimit'))
                    unit = networkTraffic.get('datatrafficUnit')
                    host.network_traffic_limit = raw_limit * byte_multiplier(unit)

            location = server.get('location')
            if location:
                host.datacenter = location.get('site')

            specs = server.get('specs')
            if specs:
                cpu = specs.get('cpu')
                if cpu:
                    host.cpu = cpu.get('type')
                    host.socket_count = cpu.get('quantity')
                ram = specs.get('ram')
                if ram:
                    size = int(ram.get('size'))
                    unit = ram.get('unit')
                    host.ram = size * byte_multiplier(unit)
                hdd = specs.get('hdd')
                if hdd:
                    for hdd_group in hdd:
                        amount = int(hdd_group.get('amount'))
                        size = int(hdd_group.get('size'))
                        unit = hdd_group.get('unit')
                        host.hdd = amount * size * byte_multiplier(unit)

            #print(host.to_dict())

            # Create and append a result for this server.
            result = Result()
            encoder = SwaggerJsonEncoder()
            result.data = encoder.encode(host)
            result.result_type = "Host"
            result.data_series_id = data_series_id
            results.append(result)

        return results

def byte_multiplier(unit):
    if unit is None:
        return 1
    normalized_unit = unit.lower()
    if normalized_unit == "kb":
        return 1024
    elif normalized_unit == "mb":
        return 1024 * 1024
    elif normalized_unit == "gb":
        return 1024 * 1024 * 1024
    else:
        return 1
