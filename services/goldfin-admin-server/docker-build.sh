#!/bin/bash
# Build admin server Docker image including all dependencies. 
set -e

# Build file for Docker services. 
cd `dirname $0`

# Clean the build to ensure we don't pick up obsolete libraries. 
mvn clean

# Build the UI distribution.  It's required by the docker file and must be
# in-tree for the docker build to work. 
rm -rf target/ui
mkdir -p target/ui
(cd ../goldfin-ui/goldfin-ui-app; ng build --base-href=/ui/ --environment=prod)
rsync -avr ../goldfin-ui/goldfin-ui-app/dist/ target/ui

# Run 'mvn install' which generates the Docker file for us. (No need to invoke
# docker directly.)
mvn install -DskipTests
