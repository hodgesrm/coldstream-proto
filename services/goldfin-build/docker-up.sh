#!/bin/bash

# Bring up composed containers.
set -x
docker-compose -p goldfin up -d
