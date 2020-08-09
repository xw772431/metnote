#!/bin/bash

VERSION=$(ls build/libs | sed 's/.*metnote-//' | sed 's/.jar$//')

echo "Metnote version: $VERSION"

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build --build-arg JAR_FILE=build/libs/metnote-$VERSION.jar -t $DOCKER_USERNAME/metnote -t $DOCKER_USERNAME/metnote:$VERSION .
docker images
docker push $DOCKER_USERNAME/metnote
