#!/bin/bash

# Define versioning variables
FAMILY=1
VERSION=2
CONSTRUCTION=3  # Update as needed

# File paths
JAVA_FILE="src/main/GrapherMain.java"
RELEASES_DIR="releases"
README_FILE="README.md"
OUTPUT_FILE="$RELEASES_DIR/Graphing-v$FAMILY.$VERSION.$CONSTRUCTION.jar"
JAR_NAME="Graphing-v$FAMILY.$VERSION.$CONSTRUCTION.jar"

# Step 1: Remove previous releases matching the naming pattern
echo "::Removing previous releases..."
find "$RELEASES_DIR" -name "Graphing-*.jar" -type f -exec rm {} \;

# Step 2: Update the Java file with the new version numbers
sed -i "s/\(final int\s*family\s*=\s*\)[0-9]\+;/\1$FAMILY;/" "$JAVA_FILE"
sed -i "s/\(final int\s*version\s*=\s*\)[0-9]\+;/\1$VERSION;/" "$JAVA_FILE"
sed -i "s/\(final int\s*construcction\s*=\s*\)[0-9]\+;/\1$CONSTRUCTION;/" "$JAVA_FILE"

echo "::Updated $JAVA_FILE with family=$FAMILY, version=$VERSION, and construcction=$CONSTRUCTION"

# Step 3: Build process
rm -r build 2>/dev/null
javac --source-path src/ -d build src/main/GrapherMain.java --release 9
cp -r icons/ build/main/
cd build || exit
jar cfm "../$OUTPUT_FILE" ../releases/Manifest.txt main/*
cd ..

echo "::Build complete: $OUTPUT_FILE"

# Step 4: Update the README file with the new JAR file name
sed -i "s/Graphing-v[0-9]*\.[0-9]*\.[0-9]*\.jar/$JAR_NAME/" "$README_FILE"

echo "::Updated $README_FILE with the new JAR file name: $JAR_NAME"
