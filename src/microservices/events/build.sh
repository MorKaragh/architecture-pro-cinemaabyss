#!/bin/bash

# Build script for Events Service

echo "Building Events Service..."

# Clean and package
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file created: target/events-service-1.0.0.jar"
else
    echo "Build failed!"
    exit 1
fi
