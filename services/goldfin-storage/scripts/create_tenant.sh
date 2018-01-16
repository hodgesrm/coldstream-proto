#!/bin/bash
uuid=$(uuidgen)
curl -H "Content-Type: application/json" -X POST \
-d '{"tenantId":"'$uuid'","name":"tenant-1"}' \
http://localhost:5999/api/v1/tenant-data

