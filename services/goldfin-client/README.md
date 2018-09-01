# Client Guide

## Setup

```shell
[tar -xf goldfin-client.tar.gz]
[cd goldfin-client]
python3 -m venv venv
. venv/bin/activate
python3 setup.py install
```

## Configuration 

Set up data_config.yaml with the following data. Tags can have any
string names or values you desire.
```
# Configuration file for data loading. 

# Connection information to API server.  This is used to upload
# observations to Goldfin server. 
api_server:
  host: localhost
  port: 443
  secret_key: "<Goldfin AI key goes here>"
  verify_ssl: false

# General tags that should be assigned to any data series selected using
# this configuration file.
tags:
  account: test@skylineresearch.com

# Probe definitions including provider type, parameters (largely credentials), 
# and tags that should be assigned to observations from each probe.
data_probes:
  - name: leaseweb
    provider: leaseweb
    provider_params:
      api_key: "<Vendor API goes here>"
    tags:
      company: "skylineresearch-inc"
      business_unit: "marketing"
```

## Collecting Data

Run data_collector at daily intervals.  The following command executes
all data probes and uploads to the API server. 
```shell
. venv/bin/activate
data_collector run --all-probes \
--config data_config.yaml \
--out /tmp/collector --upload
```
The data_collector generates one or more observation files, which are write
to the directory selected by the --out option. 

You can also run individual probes and upload later (e.g., through the UI) as 
shown in the following example. 

```shell
# Does not work yet!
data_collector upload --probes leaseweb --out /tmp/collector
--config ~/coldstream/inventory/data_config.yaml 
```
