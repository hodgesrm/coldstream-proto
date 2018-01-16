#!/bin/bash
tenantId=$1
docId=$2
set -x
curl -H "Content-Type: application/octet-stream" \
http://localhost:5999/api/v1/tenant-data/${tenantId}/document/${docId}

