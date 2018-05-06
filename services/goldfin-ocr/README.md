# Server Operation Guide

## Setup

```shell
python3 setup.py 
```

## Build Docker Image
```shell
docker build -t goldfin-ocr .
```

## Server start 
```shell
. ~/coldstream/python/pycharm/python/bin/activate
./goldfin_ocr/scanctl.py --daemon --log-level=INFO [--iterations=5]
```

## Repeat local scan for testing. 
```shell
python3 goldfin_ocr/scanctl.py --body test-doc.json --request=scan --ocr-cfg=tests/ocr-test.yaml
```

## Run a single provider for development/testing.
```shell
python3 goldfin_ocr/table/invoicectl.py --xml /tmp/ocr/c80bf863-a161-4357-b92a-c238a1ea421d_1/2017.746669\[3\]-printed.pdf.xml [ --log-level DEBUG ]
```
