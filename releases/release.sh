#!/bin/bash

# Define versioning variables
FAMILY=1
VERSION=3
CONSTRUCTION=1

# File paths
JAVA_FILE="src/main/GrapherMain.java"
RELEASES_DIR="releases"
README_FILE="README.md"
LIB_DIR="libraries"
BUILD_DIR="build"
JAR_NAME="Graphing-v$FAMILY.$VERSION.$CONSTRUCTION.jar"
OUTPUT_FILE="$RELEASES_DIR/$JAR_NAME"

# Step 1: Cleanup
echo ":: Cleaning previous builds and releases..."
rm -rf "$BUILD_DIR" 2>/dev/null
mkdir -p "$BUILD_DIR"
find "$RELEASES_DIR" -name "Graphing-*.jar" -type f -exec rm {} \;

# Step 2: Update Java constants
sed -i "s/\(final int\s*family\s*=\s*\)[0-9]\+;/\1$FAMILY;/" "$JAVA_FILE"
sed -i "s/\(final int\s*version\s*=\s*\)[0-9]\+;/\1$VERSION;/" "$JAVA_FILE"
sed -i "s/\(final int\s*construction\s*=\s*\)[0-9]\+;/\1$CONSTRUCTION;/" "$JAVA_FILE"
echo ":: Updated version constants in $JAVA_FILE"

# Step 3: Compilation
# Dynamically create the classpath from all JARs in the libraries folder
CLASSPATH=$(find "$LIB_DIR" -name "*.jar" | tr '\n' ':')

echo ":: Compiling with libraries..."
javac -cp "$CLASSPATH" -d "$BUILD_DIR" src/main/*.java --release 9

# Step 4: Prepare the Fat JAR (Extracting all libraries into build)
echo ":: Unpacking libraries for Fat JAR..."
cp -r icons/ "$BUILD_DIR/main/" 2>/dev/null

cd "$BUILD_DIR" || exit
for jar in ../"$LIB_DIR"/*.jar; do
    jar xf "$jar"
done
# Remove META-INF from libraries to prevent manifest conflicts
rm -rf META-INF/*.SF META-INF/*.DSA META-INF/*.RSA 2>/dev/null
cd ..

# Step 5: Packaging
echo ":: Packaging $JAR_NAME..."
cd "$BUILD_DIR" || exit
jar cfm "../$OUTPUT_FILE" ../releases/Manifest.txt *
cd ..

# Step 6: Update Documentation
sed -i "s/Graphing-v[0-9]*\.[0-9]*\.[0-9]*\.jar/$JAR_NAME/g" "$README_FILE"

echo "---------------------------------------"
echo ":: Build Success: $OUTPUT_FILE"
echo ":: Documentation updated in $README_FILE"


# # ===============================================================================
# Check:
# - Save function


# #!/bin/bash

# # Define versioning variables
# FAMILY=1
# VERSION=3
# CONSTRUCTION=1

# # File paths
# JAVA_FILE="src/main/GrapherMain.java"
# RELEASES_DIR="releases"
# README_FILE="README.md"
# LIB_DIR="libraries"
# FLATLAF_JAR="$LIB_DIR/flatlaf-3.4.jar"
# OUTPUT_FILE="$RELEASES_DIR/Graphing-v$FAMILY.$VERSION.$CONSTRUCTION.jar"
# JAR_NAME="Graphing-v$FAMILY.$VERSION.$CONSTRUCTION.jar"

# # Step 1: Remove previous releases matching the naming pattern
# echo "::Removing previous releases..."
# find "$RELEASES_DIR" -name "Graphing-*.jar" -type f -exec rm {} \;

# # Step 2: Update the Java file with the new version numbers
# sed -i "s/\(final int\s*family\s*=\s*\)[0-9]\+;/\1$FAMILY;/" "$JAVA_FILE"
# sed -i "s/\(final int\s*version\s*=\s*\)[0-9]\+;/\1$VERSION;/" "$JAVA_FILE"
# sed -i "s/\(final int\s*construction\s*=\s*\)[0-9]\+;/\1$CONSTRUCTION;/" "$JAVA_FILE"

# echo "::Updated $JAVA_FILE with family=$FAMILY, version=$VERSION, and construction=$CONSTRUCTION"

# # Step 3: Build process
# rm -r build 2>/dev/null

# # javac -cp "$FLATLAF_JAR" --source-path src/ -d build src/main/*.java --release 9
# javac -cp "$FLATLAF_JAR" -d build src/main/*.java --release 9
# cp -r icons/ build/main/

# cd build
# jar xf ../libraries/flatlaf-3.4.jar
# cd ../

# cd build || exit
# jar cfm "../$OUTPUT_FILE" ../releases/Manifest.txt *
# cd ..

# echo "::Build complete: $OUTPUT_FILE"

# # Step 4: Update the README file with the new JAR file name
# sed -i "s/Graphing-v[0-9]*\.[0-9]*\.[0-9]*\.jar/$JAR_NAME/" "$README_FILE"

# echo "::Updated $README_FILE with the new JAR file name: $JAR_NAME"
