#!/bin/bash

echo "Cleaning up stopped containers and unused images"
docker container prune -f
docker image prune -f

echo "Cleaning existing goldfin images (to prevent build errors)"
docker image rmi $( docker images 'goldfin/datactl' -q) 
docker image rmi $( docker images 'goldfin/scanctl' -q)
docker image rmi $( docker images 'goldfin/admin-server' -q)
docker images |grep goldfin
if [ $? == "0" ]; then
  echo "Did not delete all goldfin images, see list"
else
  echo "All goldfin images deleted"
fi
