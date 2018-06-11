#!/bin/bash

# Upload observations efficiently.
SCRIPT_DIR=`dirname $0`
CLI="$SCRIPT_DIR/../../../goldfin-admin-server/target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1/bin/svc-client"
if [ ! -x $CLI ]; then
  echo "Can't find client: $CLI"
  exit 1 
else
  echo "Using CLI: [${CLI}]"
fi

set -e
echo "Executing login..."
$CLI login --host localhost --user=test@skylineresearch.com --password=secret12

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
