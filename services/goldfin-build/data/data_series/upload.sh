#!/bin/bash

# Upload observations efficiently.
SCRIPT_DIR=`dirname $0`
CLI="$SCRIPT_DIR/../../../goldfin-admin-server/target/goldfin-admin-server-0.9.1-distribution/goldfin-admin-server-0.9.1/bin/svc-client"
if [ ! -x $CLI ]; then
  echo "Can't find client: $CLI"
  exit 1 
else
  echo "Using CLI: [${CLI}]"
fi

if [ -z "$HOST" ]; then
  HOST=localhost
fi

set -e
echo "Executing login to $HOST..."
#$CLI login --host $HOST --port 8443 --user=admin@goldfin.io \
#--password=secret12 --verbose
$CLI login --host $HOST --port 8443 --user=test@skylineresearch.com \
--password=secret12 --verbose

if [ "$1" = "all" ]; then
  echo "Getting all observations!"
  FILES=`cd observations; ls`
else
  FILES="GrandCoulee-2018-05-01_12:00:00.json \
  GrandCoulee-2018-05-02_12:00:00.json \
  GrandCoulee-2018-05-03_12:00:00.json \
  GrandCoulee-2018-05-04_12:00:00.json \
  GrandCoulee-2018-05-05_12:00:00.json \
  GrandCoulee-2018-05-06_12:00:00.json \
  GrandCoulee-2018-05-07_12:00:00.json \
  GrandCoulee-2018-05-08_12:00:00.json \
  GrandCoulee-2018-05-09_12:00:00.json \
  GrandCoulee-2018-05-10_12:00:00.json \
  "
fi

set +e
for f in $FILES; do
  echo "Uploading: ${f}..."
  $CLI data-create --file observations/$f --description "Grand Coulee test"
done
