# Server Operation Guide

## Building. 'mvn install' required UI to be built and in target/ui directory.
```shell
mvn clean package
```

## Full build to docker image. This creates the UI. 
```
./docker-build.sh
```

## Server start (console mode with debug enabled).
```shell
# Start postgresql image. 
docker run -e POSTGRES_PASSWORD=secret -p 15432:5432 -d postgres
# Start server. 
cd target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1
bin/admin-server console debug
```

## Prerequisites

For dev usage, GOLDFIN_CONFIG_DIR should point to directory with all config
files. 
```
export GOLDFIN_CONFIG_DIR=$HOME/goldfin-dev/conf
```

For prod deployment, config files go in /var/lib/goldfin/conf. 

## Service Initialization and Removal 

Here are the steps to create a new service. 

1. Create an init-params.yaml file in `$GOLDFIN_CONFIG_DIR/init-params.yaml`. 
2. Create an service.yaml in `$GOLDFIN_CONFIG_DIR/service.yaml`.
3. Run servicectl init. 
```shell
# Must run in dist location for relative directory references to work. 
cd target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1
svc-init create --init-params=$GOLDFIN_CONFIG_DIR/init-params.yaml \
     --service-config=$GOLDFIN_CONFIG_DIR/service.yaml
```
The service-config.yaml file defines parameters used across all services. 

Here are step(s) to remove a service. 

1. Stop any running images
2. Using previous init.params file issue the following command: 
```shell
svc-init remove --init-params=$GOLDFIN_CONFIG_DIR/init-params.yaml\
 --service-config=$GOLDFIN_CONFIG_DIR/service.yaml
```

## Start API server

After creating the service data start an API server. 

## Create a tenant. 
```shell
svc-client login --host localhost --user=sysadmin@system --password=secret12
svc-client tenant-create --name 'skylineresearch.com' \
--description "Skyline Research, Inc." --schema-suffix=skyline
svc-client tenant-list
# Get tenantId. 
svc-client user-create --initialPassword=secret12 \
--user=test@skylineresearch.com 
```

### Load invoice for client. 
```shell
svc-client login --host localhost --user=test@skylineresearch.com \
--password=secret12
svc-client document-create --description='Test invoice' \
--file /home/rhodges/coldstream/invoices/ovh/invoice_WE666184.pdf
svc-client document-list
```

## Running Docker Images

Look for image using docker images.  You'll see something like: 

```
REPOSITORY                  TAG                 IMAGE ID            CREATED             SIZE
goldfin/rest-api            0.0.1               e9b2532f7d18        8 seconds ago       645MB
```

Start the docker image using the IMAGE ID tag. 

```shell
docker -l debug run -it -p 7443:8443 e9b2532f7d18
```

## Clean up Docker Images

```shell
docker images -q --filter dangling=true |xargs docker rmi
```

## REST Service Login

```shell
curl -d '{"user":"sysadmin", "password":"secret"}' \
-H "Content-Type: application/json" \
-X POST https://localhost:8443/api/v1/login -v -k
```

## Generate New Cert

Run bin/make-cert, and put resulting keystore.jks file in conf directory.
A default file is already supplied, as are server-config.yaml. 
