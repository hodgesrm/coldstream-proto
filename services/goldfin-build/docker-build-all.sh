#!/bin/bash

# Invoke build on all docker services. 
set -e
set -x
../goldfin-ocr/docker-build.sh
../goldfin-admin-server/docker-build.sh
../goldfin-ui/docker-build.sh
