#!/bin/bash
uuid=$1 
file=$2
set -x
curl -H "Content-Type: multipart/form-data" -X POST \
-F "file=@${file};type=application/octet-stream" \
-F "name=ls" \
-F "description=Test File" \
http://localhost:5999/api/v1/tenant-data/$1/document -vvv

