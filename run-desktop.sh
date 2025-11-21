#!/bin/bash
# Hotel Management Platform - Desktop Application Launcher
# This script sets environment variables and launches the desktop GUI
# Usage: ./run-desktop.sh

echo "========================================"
echo "Hotel Management Platform"
echo "Starting Desktop Application..."
echo "========================================"
echo ""

# Load environment variables from set-env.sh
if [ -f "./set-env.sh" ]; then
    echo "Loading environment variables..."
    source ./set-env.sh
    echo ""
else
    echo "ERROR: set-env.sh not found!"
    echo "Please create set-env.sh with your database credentials."
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven (mvn) not found in PATH!"
    echo "Please install Maven and add it to your PATH."
    exit 1
fi

echo "Starting desktop application..."
echo ""

# Run the desktop application
mvn exec:java -Dexec.mainClass="com.hotel.desktop.DesktopLauncher"

