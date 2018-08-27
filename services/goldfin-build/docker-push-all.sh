#!/bin/bash

# Pull latest docker images to Dockerhub private repo. 
set -e
set -x
docker push goldfin/admin-server:0.9.1
docker push goldfin/datactl:0.9.1
docker push goldfin/scanctl:0.9.1
