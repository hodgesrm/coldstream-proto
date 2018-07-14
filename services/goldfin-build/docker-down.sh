#!/bin/bash

# Take down composed containers.
set -x
docker-compose -p goldfin down 
