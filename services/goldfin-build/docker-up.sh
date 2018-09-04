#!/bin/bash

# Bring up composed containers.
export VERSION=$1
if [ "$VERSION" = "" ]; then
  echo "Usage $0 <version>"
  exit 1
fi
set -x
docker-compose -p goldfin up -d
