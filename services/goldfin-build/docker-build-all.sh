#!/bin/bash

# Invoke build on all docker services. 
cd `dirname $0`
if [ "$VERSION" = "" ]; then
  export VERSION=$(cat ../VERSION)
fi
set -e
set -x
../goldfin-ocr/docker-build.sh $VERSION
../goldfin-admin-server/docker-build.sh $VERSION
