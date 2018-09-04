# Server Operation Guide

## Setup

```shell
. ~/coldstream/python/pycharm/python/bin/activate
python3 setup.py 
```

## Run Tests
```shell
# Start venv
python3 setup.py develop
```

## Run Tests

```shell
# Start venv
python3 -m unittest tests/test_*.py -v
```

## Build Docker Images
```shell
# OCR service container.
docker build -f Dockerfile.ocr -t goldfin-ocr .
# Data service container.
docker build -f Dockerfile.data -t goldfin-data .
```

## OCR Operation Examples
### OCR Server start 
```shell
./goldfin_ocr/scanctl.py --daemon --log-level=INFO [--iterations=5]
```

### Repeat OCR scan for testing. 
```shell
python3 goldfin_ocr/scanctl.py --body test-doc.json --request=scan --ocr-cfg=tests/ocr-test.yaml
```

### Run a single provider for development/testing.
```shell
python3 goldfin_ocr/table/invoicectl.py --xml /tmp/ocr/c80bf863-a161-4357-b92a-c238a1ea421d_1/2017.746669\[3\]-printed.pdf.xml [ --log-level DEBUG ]
```

## Data Series Operation Examples
### Data Series Server start 
```shell
./goldfin_ocr/datactl.py --daemon --log-level=INFO [--iterations=5]
```

### Repeat data series processing for testing. 
```shell
python3 goldfin_ocr/scanctl.py --body \
  /tmp/data_series/3391dbee-2120-4de8-8262-bda800984032_1/data_series.json
```
