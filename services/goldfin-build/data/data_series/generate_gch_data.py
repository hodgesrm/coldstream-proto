#!/usr/bin/env python3
#
# Copyright (c) 2018 Goldfin.  All rights reserved.

"""Utility to create Goldfin test data"""

import argparse
import datetime
import json
import os
import sys

from goldfin.client.json_xlate import SwaggerJsonEncoder, model_to_json_dict
import goldfin_ocr.vendors as vendors
from goldfin.client.api.models import Host
from goldfin.client.api.models import Observation

# Utility functions. 
def str_to_date(date_string):
    return datetime.datetime.strptime(date_string, '%m/%d/%y')

def date_to_str(date):
    return date.strftime('%Y-%m-%d')

# Handy constants.
MB = 1024 * 1024
GB = 1024 * 1024 * 1024

FE = {
  'host_type': 'BARE_METAL',
  'host_model': 'Front End',
  'cpu': 'Intel Xeon Processor E3-1240 v3', 
  'socket_count': 1,
  'core_count': 4,
  'thread_count': 8,
  'ram': 16 * GB,
  'hdd': 1 * 1024 * GB,
  'ssd': 0, 
  'nic_count': 2,
  'network_traffic_limit': 100 * MB,
  'backup_enabled': 'True' 
}

BE = {
  'host_type': 'BARE_METAL',
  'host_model': 'Front End',
  'cpu': 'Intel Xeon Processor E5-2630L v4',
  'socket_count': 1,
  'core_count': 10,
  'thread_count': 20,
  'ram': 64 * GB,
  'hdd': 0,
  'ssd': 2 * 512 * GB, 
  'nic_count': 2,
  'network_traffic_limit': 100 * MB,
  'backup_enabled': 'True' 
}

SBE = {
  'host_type': 'BARE_METAL',
  'host_model': 'Front End',
  'cpu': 'Intel Xeon Processor E5-2630L v4',
  'socket_count': 2,
  'core_count': 10,
  'thread_count': 20,
  'ram': 128 * GB,
  'hdd': 0,
  'ssd': 4 * 1024 * GB, 
  'nic_count': 4,
  'network_traffic_limit': 10000 * MB,
  'backup_enabled': 'True' 
}

# Host definitions. 
HOST_DEFINITIONS = [
    { 
        'host_id': '13351',
        'resource_id': 'NQXF003', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '13352',
        'resource_id': 'NQXF005', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('06/30/18')
    },
    { 
        'host_id': '13353',
        'resource_id': 'NQXF006', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '13355',
        'resource_id': 'NQXF008', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '13356',
        'resource_id': 'NQXF009', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '13400',
        'resource_id': 'NQXF010', 
        'hw': FE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '79975',
        'resource_id': 'NQXB089', 
        'hw': BE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '79976',
        'resource_id': 'NQXB090', 
        'hw': BE, 
        'start': str_to_date('05/01/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '79990',
        'resource_id': 'NQXB092', 
        'hw': BE, 
        'start': str_to_date('05/11/18'),
        'end': str_to_date('07/31/18')
    },
    { 
        'host_id': '112529',
        'resource_id': 'NQZC115', 
        'hw': SBE, 
        'start': str_to_date('07/14/18'),
        'end': str_to_date('07/31/18')
    }
]


# Define top-level command line parsing.
parser = argparse.ArgumentParser(prog='generate_gch_data.py',
                                 usage="%(prog)s [options]")
parser.add_argument("--out-dir",
                    help="Output directory for observations (default: %(default)s)", 
                    default="observations")
parser.add_argument("--start",
                    help="Start date in mm/dd/yy format (default: %(default)s)",
                    default="05/01/18")
parser.add_argument("--end",
                    help="End date in mm/dd/yy format (default: %(default)s)",
                    default="07/31/18")
parser.add_argument("--verbose",
                    help="Print verbose output to see what's happening")

# Process options.  This will automatically print help. 
args = parser.parse_args()

# Ensure we have a writable output directory. 
if args.out_dir is None:
    print_error("You must specify an output directory --out-dir")
    sys.exit(1)
os.makedirs(args.out_dir, exist_ok=True)
if not os.path.exists(args.out_dir) or not os.access(args.out_dir, os.W_OK):
    print_error("Output directory does not exist or is not writable: {0}".format(args.out_dir))
    sys.exit(1)

# Loop through the dates from start to finish. 
start_date = str_to_date(args.start) 
end_date = str_to_date(args.end) 
verbose = args.verbose is not None

# Use generator to iterate over dates. 
effective_date = start_date
while effective_date <= end_date:
    # Use a date that has an offset added for more realism.
    obs_date = effective_date + datetime.timedelta(hours=12)
    #obs_date_as_str = str(effective_date + datetime.timedelta(hours=12))
    if verbose:
        print(date_to_str(effective_date))
    # Generate host information. Each host is separately serialized to 
    # a string. 
    data = []
    for host_def in HOST_DEFINITIONS:
        if host_def['start'] > effective_date:
            if verbose:
                print("Host starts later: " + str(host_def))
            continue
        elif host_def['end'] < effective_date:
            if verbose:
                print("Host ends earlier: " + str(host_def))
            continue

        host = Host()
        host.host_id = host_def['host_id']
        host.resource_id = host_def['resource_id']
        #host.effective_date = obs_date_as_str
        host.effective_date = obs_date

        host.region = 'USA'
        host.zone = 'WASH-1'
        host.datacenter = 'WASH-1-A'

        hw = host_def['hw']
        host.host_type = hw['host_type']
        host.model = hw['host_model']
        host.cpu = hw['cpu']
        host.socket_count = hw['socket_count']
        host.core_count = hw['core_count']
        host.thread_count = hw['thread_count']
        host.ram = hw['ram']
        host.hdd = hw['hdd']
        host.ssd = hw['ssd']
        host.nic_count = hw['nic_count']
        host.network_traffic_limit = hw['network_traffic_limit']
        host.backup_enabled = hw['backup_enabled']

        # We encode to JSON to force translation of names from snake to
        # camel case.  We then reload as a map and add to the array. 
        host_encoder = SwaggerJsonEncoder(sort_keys=True)
        host_content = host_encoder.encode(host)
        host_content_reloaded = json.loads(host_content)
        if verbose:
            print(host_content_reloaded)
        data.append(host_content_reloaded)

    # Create observation, serialize to JSON string, and add to data array.
    obs = Observation()
    obs.vendor_identifier = vendors.GRAND_COULEE
    obs.effective_date = obs_date
    obs.observation_type = "HOST_INVENTORY"
    obs.data = json.dumps(data, sort_keys=True)
 
    # Serialize to file. 
    name = "{0}-{1}.json".format(obs.vendor_identifier, obs_date.strftime("%Y-%m-%d_%H:%M:%S"))
    output_file = os.path.join(args.out_dir, name)
    with open(output_file, "w") as obs_file:
        print("Writing observation file: {0}".format(output_file))
        if verbose:
            print(str(obs.to_dict()))
        encoder = SwaggerJsonEncoder(sort_keys=True)
        content = encoder.encode(obs)
        obs_file.write(content)

    # On to the next one. 
    effective_date += datetime.timedelta(days=1)

print("Done!")
