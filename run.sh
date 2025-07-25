#!/bin/bash
echo "Compiling Space Dodger..."
cd src
javac *.java
if [ $? -ne 0 ]; then
    echo "Compilation failed! Make sure Java is installed and in your PATH."
    exit 1
fi
echo "Compilation successful!"
echo ""
echo "Starting Space Dodger..."
java SpaceDodger