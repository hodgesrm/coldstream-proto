# Client Guide

## Before Getting Started...

You'll need Python version 3.5 or higher to use Goldfin.  Type `python3
--version` in a command window to see which version you have.  If it's
less than 3.5, install a new version from the following website:

  https://www.python.org/downloads/

After the installation type `python3 --version` again to ensure you have
the new version.  If not, try logging out and logging back in again.

## Installation and Upgrade

Goldfin data collectors are written in Python and distributed in a tar.gz
file with a name like goldfin_client-1.0.1.tar.gz. 

The following steps install the Goldfin data collector in a Python
virtual environment.  A virtual environment is a private set of Python
libraries that can be installed and upgraded without affecting Python
use elsewhere on your machine.

```shell
python3 -m venv venv
. venv/bin/activate
pip3 install releases/goldfin_client-1.0.1.tar.gz 
```

If a new version of the collector is released, you can upgrade as follows.
```shell
. venv/bin/activate
pip3 install --upgrade releases/goldfin_client-1.0.2.tar.gz 
```

## Configuration 

Set up data_config.yaml with the following data. Tags can have any
string names or values you desire. Important note: tab characters are
not allowed in configuration errors and will cause errors.

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
The data_collector generates one or more observation files, which are written
to the directory selected by the --out option. 

You can also run individual probes and upload later (e.g., through the UI) as 
shown in the following example. 

```shell
# Does not work yet!
data_collector upload --probes leaseweb --out /tmp/collector
--config ~/coldstream/inventory/data_config.yaml 
```

Important note: You can clear the Python virtual environment by typing `deactivate`.  
Run the venv/bin/activate command before you try to invoke data_collector. 
