#!/bin/bash

# Build file for Docker services. 
cd `dirname $0`

# OCR service container.
docker build -f Dockerfile.ocr -t goldfin/scanctl:0.9.0 .
# Data service container.
docker build -f Dockerfile.data -t goldfin/datactl:0.9.0 .
