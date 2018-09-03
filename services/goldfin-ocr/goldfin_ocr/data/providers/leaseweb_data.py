# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""Leaseweb data analyzer"""

import json
import logging

from goldfin_ocr.json_xlate import SwaggerJsonEncoder
from goldfin_ocr.api.models.host import Host
from goldfin_ocr.api.models.observation import Observation
from goldfin_ocr.api.models.result import Result
import goldfin_ocr.vendors as vendors

from goldfin_ocr.data_utils import int_or_null
from goldfin_ocr.data.data_series_context import DataSeriesContext

# Define logger
logger = logging.getLogger(__name__)


class LeasewebDataProcessor:
    """Leaseweb Data Processor"""

    def get_results(self, observation, context):
        """Analyze observation data and return one or more results

        :param observation: An observation value.
        :type observation Observation
        :param data_series_id: Data series UUID
        :type data_series_id: str
        :param context: Context information for processing observation
        :type: context: DataSeriesContext
        :returns: list of Result
        """

        # Deserialize and dump observation data to work directory.
        server_list = json.loads(observation.data)
        context.write_work_file(json.dumps(server_list), "server_list.json")

        # Iterate over all servers in the observation and create a
        # Host instance for each one.
        results = []
        for server in server_list:
            # Starting with tags from observation, insert as much as possible 
            # into the host record.
            host = Host()
            host.tags = observation.tags
            host.vendor_identifier = vendors.LEASEWEB
            host.effective_date = observation.effective_date
            host.data_series_id = context.data_series_id
            host.host_type = "BARE_METAL"

            host.host_id = server.get('id')

            contract = server.get('contract')
            if contract:
                host.resource_id = contract.get('internalReference')
                networkTraffic = contract.get('networkTraffic')
                if networkTraffic:
                    raw_limit = int_or_null(
                        networkTraffic.get('datatrafficLimit'))
                    unit = networkTraffic.get('datatrafficUnit')
                    if raw_limit is None:
                        host.network_traffic_limit = None
                    else:
                        host.network_traffic_limit = raw_limit * byte_multiplier(
                            unit)

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
                    size = int_or_null(ram.get('size'))
                    if size is None:
                        host.ram = -1
                    else:
                        unit = ram.get('unit')
                        host.ram = size * byte_multiplier(unit)
                hdd = specs.get('hdd')
                if hdd:
                    for hdd_group in hdd:
                        amount = int_or_null(hdd_group.get('amount'))
                        size = int_or_null(hdd_group.get('size'))
                        if size is None or amount is None:
                            host.hdd = -1
                        else:
                            unit = hdd_group.get('unit')
                            host.hdd = amount * size * byte_multiplier(unit)

            # Create and append a result for this server.
            result = Result()
            encoder = SwaggerJsonEncoder()
            result.data = encoder.encode(host)
            result.result_type = "Host"
            result.data_series_id = context.data_series_id
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
    elif normalized_unit == "tb":
        return 1024 * 1024 * 1024 * 1024
    else:
        return 1