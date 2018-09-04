# Goldfin Build and Test

## Prerequisites

Installation of Docker and Docker Compose:

See https://docs.docker.com/install/linux/docker-ce/ubuntu/#install-docker-ce. 

Key commands shown below. 
```
# Add GPG key. 
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
# Verify key is there. 
sudo apt-key fingerprint 0EBFCD88
# Add repo.
sudo add-apt-repository \
 "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
 $(lsb_release -cs) \
 stable"
# Update package index.
sudo apt update
# Install docker-ce
sudo apt install docker-ce
```
Note:

To run docker commands from non-root account, edit /etc/group and add
account to docker group.

## Building 

Run docker build in all service directory to update images.

## Starting and Stopping Services

Starting the cluster.  This command runs in background, so you can't see 
the logs. 
```
./docker-up.sh <version>
```

Stopping the cluster. 
```
./docker-down.sh <version>
```

## Debug and Inspection Commands

Attach to running container with bash shell.
```
docker exec -it goldfin_api_1 /bin/bash
```

View logs for a container. (-f to follow.)
```
docker logs -f goldfin_api_1
```

Image IDs also work fine as identifiers. 

## Image cleanup

Clean up old docker images.
```
./docker-clean.sh
```

## Using built docker images. 
Login to Docker Hub and upload built images. 
```
docker login -u goldfin -p '<password>'
./docker-push-all.sh <version>
```

Start built images on another host. 
```
docker login -u goldfin -p '<password>'
export VERSION=<version you want to start>
docker-compose -p goldfin up -d
```
