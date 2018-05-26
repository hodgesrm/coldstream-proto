#!/bin/bash

# Build file for Docker services. 
cd `dirname $0`/goldfin-ui-app

# Angular CLI UI container. 
ng build --base-href=/content/dist/
docker build -t goldfin-ui-app .
