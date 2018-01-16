#!/bin/bash
uuid=$1
curl -H "Content-Type: application/json" \
http://localhost:5999/api/v1/tenant-data/$uuid

