# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""GrandCoulee data analyzer"""

import json
import logging

from goldfin_ocr.json_xlate import json_dict_to_model, SwaggerJsonEncoder
from goldfin_ocr.api.models.host import Host
from goldfin_ocr.api.models.observation import Observation
from goldfin_ocr.api.models.result import Result
import goldfin_ocr.vendors as vendors

# Define logger
logger = logging.getLogger(__name__)


class GrandCouleeDataProcessor:
    """Grand Coulee Hosting Data Processor"""

    def get_results(self, observation, data_series_id):
        """Analyze observation and return one or more results

        Grand Coulee observations consist of generated test data.  They 
        are encoded using Host record format all we have to do is 
        deserialize and return. 

        :param observation: An observation object
        :type observation Observation
        :param data_series_id: Data series UUID
        :type data_series_id: str
        :returns: list of Result
        """

        # Deserialize the observation.
        host_object_list = json.loads(observation.data)

        # Iterate over all servers in the observation and create a 
        # Host instance for each one.
        results = []
        for host_object in host_object_list:
            print("HOST: {0}".format(host_object))

            # Deserialize Host string and add additional data. 
            host_content = json.loads(host_object)
            host = json_dict_to_model(host_content, Host)
            host.data_series_id = data_series_id
            host.vendor_identifier = vendors.GRAND_COULEE

            print(host.to_dict())

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
