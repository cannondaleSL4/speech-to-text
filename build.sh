#!/bin/bash

# Number of build attempts
attempts=3

# Step 1: Maven build
for (( i=1; i<=attempts; i++ ))
do
    mvn clean install

    # Check if the build was successful
    if [ $? -eq 0 ]; then
        echo "Maven build successful!"
        break
    else
        echo "Maven build failed! Attempt $i of $attempts"
        if [ $i -eq $attempts ]; then
            echo "All build attempts have failed!"
            exit 1
        fi
    fi
done

# Step 2: Docker build
docker build -t speech-to-text .

if [ $? -ne 0 ]; then
    echo "Docker build failed!"
    exit 1
fi

echo "Successfully built the Docker image: speech-to-text"

# Step 3: Load Docker image into Minikube
echo "Loading Docker image into Minikube..."
docker save speech-to-text:latest | (eval $(minikube docker-env) && docker load)

if [ $? -ne 0 ]; then
    echo "Failed to load Docker image into Minikube!"
    exit 1
fi

echo "Successfully loaded Docker image into Minikube"
