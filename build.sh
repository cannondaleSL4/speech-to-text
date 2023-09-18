#!/bin/bash

# Step 1: Maven build
mvn clean install

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "Maven build failed!"
    exit 1
fi

# Step 2: Docker build
docker build -t speech-to-text .

if [ $? -ne 0 ]; then
    echo "Docker build failed!"
    exit 1
fi

echo "Successfully built the Docker image: speech-to-text"

# Step 3: Docker tag
docker tag speech-to-text:latest localhost/speech-to-text:latest

if [ $? -ne 0 ]; then
    echo "Docker tag operation failed!"
    exit 1
fi

## Step 4: Save the image to a tarball
#docker save localhost/speech-to-text:latest > speech-to-text.tar
#
#if [ $? -ne 0 ]; then
#    echo "Docker save operation failed!"
#    exit 1
#fi
#
## Step 5: Import the image to MicroK8s
#microk8s ctr image import speech-to-text.tar
#
#if [ $? -ne 0 ]; then
#    echo "MicroK8s image import failed!"
#    exit 1
#fi
#
#echo "Successfully imported the image to MicroK8s: localhost/speech-to-text:latest"
