#!/bin/bash

# Build file for Docker services. 
cd `dirname $0`
if [ "$VERSION" = "" ]; then
  export VERSION=$(cat ../VERSION)
fi

# Remove all logs. 
rm *.log

# OCR service container.
docker build -f Dockerfile.ocr -t goldfin/scanctl:${VERSION} .
# Data service container.
docker build -f Dockerfile.data -t goldfin/datactl:${VERSION} .
