# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""GrandCoulee data analyzer"""

import json
import logging

from goldfin_ocr.json_xlate import json_dict_to_model, SwaggerJsonEncoder
from goldfin_ocr.api.models.host import Host
from goldfin_ocr.api.models.observation import Observation
from goldfin_ocr.api.models.result import Result
import goldfin_ocr.vendors as vendors
from goldfin_ocr.data.data_series_context import DataSeriesContext

# Define logger
logger = logging.getLogger(__name__)


class GrandCouleeDataProcessor:
    """Grand Coulee Hosting Data Processor"""

    def get_results(self, observation, context):
        """Analyze observation and return one or more results

        Grand Coulee observations consist of generated test data.  They 
        are encoded using Host record format all we have to do is 
        deserialize and return. 

        :param observation: An observation object
        :type observation Observation
        :param context: Context information for processing observation
        :type: context: DataSeriesContext
        :returns: list of Result
        """

        # Deserialize and dump observation data to work directory.
        host_object_list = json.loads(observation.data)
        context.write_work_file(json.dumps(host_object_list), "host_object_list.json")

        # Iterate over all servers in the observation and create a 
        # Host instance for each one.
        results = []
        for host_content in host_object_list:

            # Deserialize Host string and add additional data. 
            #host_content = json.loads(host_object)
            host = json_dict_to_model(host_content, Host)
            host.data_series_id = context.data_series_id
            host.vendor_identifier = vendors.GRAND_COULEE

            # Create and append a result for this server.
            result = Result()
            encoder = SwaggerJsonEncoder()
            result.data = encoder.encode(host)
            result.result_type = "Host"
            result.data_series_id = context.data_series_id
            results.append(result)

        return results
