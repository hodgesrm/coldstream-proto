# Client Guide

## Setup

```shell
python3 setup.py develop
```

## Check inventory. 

```shell
# Use defaults.
python3 goldfin/client/inventory_scan.py --inventory leaseweb --log-level=DEBUG 

# Redirect inventory.ini and output from/to alternative locations. 
inventory_scan --inventory leaseweb \
--config ~/coldstream/inventory/inventory.ini \
--out-dir ~/coldstream/inventory/data 
```
