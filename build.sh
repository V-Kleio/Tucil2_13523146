#!/usr/bin/bash

echo "Removing compiled files..."
rm -rf bin/*

echo "Compiling java code..."
find src -name "*.java" > sources.txt
javac -d bin/ @sources.txt
rm sources.txt

echo "Running the main program..."
java -cp bin Main