#!/bin/bash
# Configure Ubuntu AMI to run docker. 

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
sudo apt -y install docker-ce
# Install docker-compose. 
sudo curl -L https://github.com/docker/compose/releases/download/1.21.2/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod 755 /usr/local/bin/docker-compose 
# Add ubuntu user to docker group. 
sudo usermod -aG docker ubuntu
