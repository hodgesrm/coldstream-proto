# Server Operation Guide

## Building

```shell
mvn clean install
```

## Service Initialization and Removal 

Here are the steps to create a new service. 

1. Create an init-params.yaml file from template in conf/init-params.yaml.sample. 
1. Run servicectl init. 
```shell
servicectl init --init-params=$PWD/conf/init-params.yaml --dbms-config=dbms-config.yaml
```
The dbms-config.yaml file is required by the running image. 

Here are step(s) to remove a service. 

1. Stop any running images
1. Using previous init.params file issue the following command: 
```shell
   servicectl remove --init-params=$PWD/conf/init-params.yaml
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
A default file is already supplied, as are server-config.yaml and
dbms-config.yaml.
