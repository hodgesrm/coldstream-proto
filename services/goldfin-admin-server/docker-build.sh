#!/bin/bash

# Build file for Docker services. 
cd `dirname $0`

# Java docker file.
mvn install -DskipTests
