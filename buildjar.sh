#!/usr/bin/bash

echo "Removing compiled files..."
rm -rf bin/*
mkdir -p bin

echo "Creating manifest file..."
echo "Manifest-Version: 1.0" > manifest.txt
echo "Main-Class: Main" >> manifest.txt

echo "Compiling java code..."
find src -name "*.java" > sources.txt
javac -d bin/ @sources.txt
rm sources.txt

echo "Creating executable JAR..."
jar cvfm QuadtreeCompression.jar manifest.txt -C bin .
rm manifest.txt

echo "Done!"
echo "java -jar QuadtreeCompression.jar"