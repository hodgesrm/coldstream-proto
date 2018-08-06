# Client Guide

## Setup

```shell
python3 setup.py develop
```

## Check inventory. 

```shell
# Use defaults.
python3 goldfin/client/data_collector.py --collector leaseweb --log-level=DEBUG 

# Redirect inventory.ini and output from/to alternative locations. 
data_collector --collector leaseweb \
--config ~/coldstream/inventory/data_collector.ini \
--out-dir ~/coldstream/inventory/data 
```
