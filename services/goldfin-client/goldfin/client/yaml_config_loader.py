# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Load and validate YAML"""

import logging
import os
import yaml

from goldfin.client.collectors.ifactory import get_provider

# Standard logging initialization.
logger = logging.getLogger(__name__)

class YamlValidationError(BaseException):
    def __init__(self, msg, config_path):
        self.msg = msg
        self.config_path = config_path

def load_yaml(config_path):
    """Load and validate YAML configuration file

    Returns dict result if successful, otherwise raises an exception

    :param config_path: Path to configuration file
    :type config_path: str
    :returns: dict
    """
    # Step 1: Load the file. 
    if not os.path.exists(config_path):
        raise YamlValidationError("Config file not found", config_path)
    with open(config_path, "r") as config_file:
        config = yaml.load(config_file)

    # 2. Check for required sections. 
    api_server = config.get('api_server')
    if api_server is None:
        raise YamlValidationError("Config file needs an api_server section to locate Goldfin server", config_path)

    required_params = ['host', 'port', 'secret_key']
    for key in required_params:
       val = api_server.get(key)
       if val is None or val == "":
           raise YamlValidationError("The api_server section is missing a key: {0}".format(key), config_path)

    data_probes = config.get('data_probes')
    if data_probes is None:
        raise YamlValidationError("Config file needs a data_probes section to describe data to collect", config_path)

    # 3. Iterate over the probes and validate each one.
    for n, probe in zip(range(1, len(data_probes)), data_probes):
        name = probe.get('name')
        if name is None or name == "":
            raise YamlValidationError("Probe #{0} missing name".format(n), config_path)

        provider = probe.get('provider')
        provider_params = probe.get('provider_params')
        try: 
            provider = get_provider(provider, provider_params)
        except Exception:
            raise YamlValidationError("Probe {0} provider is invalid".format(name), config_path)

    # If we got to this point the file is OK.
    return config
