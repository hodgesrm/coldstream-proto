#!/bin/bash

export GOLDFIN_VERSION=0.9.1

# Build file for Docker services. 
cd `dirname $0`

# OCR service container.
docker build -f Dockerfile.ocr -t goldfin/scanctl:${GOLDFIN_VERSION} .
# Data service container.
docker build -f Dockerfile.data -t goldfin/datactl:${GOLDFIN_VERSION} .
