# Server Operation Guide

## Setup

```shell
   python3 setup.py 
```

## Server start 
```shell
   . ~/coldstream/python/pycharm/python/bin/activate
   ./goldfin_ocr/scanctl.py --daemon --log-level=INFO [--iterations=5]
```

## Run local scan for testing. 
```shell
   python3 goldfin_ocr/scanctl.py --body test-doc.json --request=scan --ocr-cfg=tests/ocr-test.yaml
```
