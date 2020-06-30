#!/bin/sh

# Building the application
./gradlew clean build

echo "Build successfully. Running the server now."

# Running docker compose
docker-compose up
