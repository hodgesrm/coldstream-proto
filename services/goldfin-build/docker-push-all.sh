#!/bin/bash

# Pull latest docker images to Dockerhub private repo. 
VERSION=$1
if [ "$VERSION" = "" ]; then
  echo "Usage: $0 <Version, e.g., 0.9.2>"
  exit 1
fi
set -e
set -x
docker push goldfin/admin-server:${VERSION}
docker push goldfin/datactl:${VERSION}
docker push goldfin/scanctl:${VERSION}
