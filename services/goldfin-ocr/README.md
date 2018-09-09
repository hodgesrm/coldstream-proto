# Server Operation Guide

## Setup

```shell
. ~/coldstream/python/pycharm/python/bin/activate
python3 setup.py develop
```

## Prerequisites for Automated Printing

OCR services (scanctl) requires CUPs to be present in order to reprint 
PDF documents to remove password protection and like ills.  This means
that first we must have the CUPS PDF driver installed as well as 
libcup2-dev installed.  Further, we need pycups,which is a wrapper over
the libcup2 API and depends on libcup2-dev to install. 
```shell
sudo apt install cups cups-pdf libcups2-dev
pip3 install pycups
```

To run CUPs jobs in a docker container, do the following:
- Ensure the Docker container uses Ubuntu 16.04.x. There are differences in 
  the cups-pdf driver if you use 18.0. 
- Copy the cups-pdf configuration files into /etc/cups.  (COPY commands shown.)
```
COPY ./conf/cups/printers.conf /etc/cups
COPY ./conf/cups/PDF.ppd /etc/cups/ppd
```
- Ensure that the cupsd daemon is started in the docker container. 
```shell
/usr/sbin/cupsd -f &
```

These latter steps are not necessary on Ubuntu dev hosts.  It's enough to 
install the drivers. 

## Run Tests

```shell
# Start venv
python3 setup.py develop
python3 -m unittest tests/test_*.py -v
```

## Build Docker Images
```shell
./docker-build.sh
```

## OCR Operation Examples
### OCR Server start 
```shell
scanctl --daemon --log-level=INFO [--iterations=5]
```

Using the docker wrapper script, which redirects logs and process stdout/err
to /var/log/goldfin.  (Also ensures cupsd is running.)
```shell
bin/scanctl-wrapper --daemon --log-level=INFO \
--log-file=/var/log/goldfin/ocr.log
```

### Repeat OCR scan for testing. 
```shell
scanctl --body test-doc.json --request=scan --ocr-cfg=tests/ocr-test.yaml
```

### Run a single provider for development/testing.
```shell
invoicectl \
--xml /tmp/ocr/c80bf863-a161-4357-b92a-c238a1ea421d_1/2017.746669.pdf.xml \
--log-level DEBUG 
```

## Data Series Operation Examples

### Data Series Server start 
```shell
datactl --daemon --log-level=INFO [--iterations=5]
```

Using the docker wrapper script, which redirects logs and process stdout/err
to /var/log/goldfin.
```shell
bin/datactl-wrapper --daemon --log-level=INFO \
--log-file=/var/log/goldfin/data.log
```

### Repeat data series processing for testing. 
```shell
datactl --body \
  /tmp/data_series/3391dbee-2120-4de8-8262-bda800984032_1/data_series.json
```
