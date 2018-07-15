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
sudo apt -y install -f docker-ce
# Add ubuntu user to docker group. 
sudo usermod -aG docker ubuntu
