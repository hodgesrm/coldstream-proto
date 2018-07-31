#!/bin/bash

echo "Cleaning up stopped containers and unused images"
set -x
docker container prune -f
docker image prune -f
