# Goldfin Deployment Procedure

## Prerequisites

Update versions to new release version in the following files.  Example: 
change 0.9.0 to 0.9.1. 

* ../VERSION
* goldfin-admin-server/pom.xml

No other versions need to be changed. 

## Build

Clean old docker images and build. 
```bash
./docker-clean.sh
./docker-build-all.sh
```
The build uses the version string in the VERSION file. 

After the build completes ensure expected docker images are available. 
```bash
docker images |grep goldfin
```
You should see exactly three images tagged with the expected version number

## Push to Docker Hub

Login to Docker Hub and upload built images. 
```bash
docker login -u goldfin -p '<password>'
./docker-push-all.sh <version>
```

## Start on Amazon Host

Push current docker-compose.yml to host, login, and start cluster.

```
scp -i ~/eng/amazon/us-west-2-preprod.pem docker-compose.yml \
 ubuntu@52.39.53.10:docker-compose.yml.new
ssh -i ~/eng/amazon/us-west-2-preprod.pem ubuntu@52.39.53.10
(On the host)
# Stop existing cluster.
docker login -u goldfin -p '<password>'
export VERSION=<old version>
docker-compose -p goldfin down
# Start new cluster.  
export VERSION=<new version>
docker-compose -p goldfin up -d
```

## DBMS Access

Login to AWS using goldfin-admin account.  Find RDS instance and 
enable public access. 

Turn off after use. 

## Preprod Service Initialization

cd $HOME/goldfin/preprod-conf
svc-init create --init-params $HOME/goldfin/preprod-conf/init-params.yaml --service-config $HOME/goldfin/preprod-conf/service.yaml 

## Debug and Inspection Commands

Attach to running container with bash shell.
```
docker exec -i -t goldfin_api_1 /bin/bash
```

View logs for a container. (-f to follow.)
```
docker logs goldfin_api_1 -f
```

## Configuration for Service Log Files

The /var/log/goldfin directory must now exist on the Docker host to hold 
service logs.
```shell
mkdir -p /var/log/goldfin
```


