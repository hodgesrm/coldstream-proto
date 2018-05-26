#!/bin/bash

# Build file for Docker services. 
cd `dirname $0`

# OCR service container.
docker build -f Dockerfile.ocr -t goldfin-ocr .
# Data service container.
docker build -f Dockerfile.data -t goldfin-data .
