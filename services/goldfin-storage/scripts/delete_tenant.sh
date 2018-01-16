#!/bin/bash
uuid=$1
curl -H "Content-Type: application/json" -X DELETE \
http://localhost:5999/api/v1/tenant-data/$uuid

