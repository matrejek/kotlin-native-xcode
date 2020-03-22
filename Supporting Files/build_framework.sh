#!/bin/sh

set -e

CHECKSUMS_FILE=$CONFIGURATION_BUILD_DIR/kotlin.checksums

buildConstellation(){
./gradlew buildForXcode
# Store CURRENT_CHECKSUMS for future builds
echo "Storing checksums for future builds..."
echo $CURRENT_CHECKSUMS > $CHECKSUMS_FILE
}

# Calcuclate checksums for c11n sources
FILE_PATHS=`find ../KotlinNativeFramework/src -type f -name "*.kt" -or -name "*.kts" | sort`
CURRENT_CHECKSUMS=`shasum $FILE_PATHS`

# Read checksums from previous build
if [[ -f "$CHECKSUMS_FILE" ]]; then
INITIAL_CHECKSUMS=$(cat $CHECKSUMS_FILE)
# Check if current checksums are different than the stored ones
OLD_HASH=$(echo -n $INITIAL_CHECKSUMS | shasum | awk '{print $1}')
NEW_HASH=$(echo -n $CURRENT_CHECKSUMS | shasum | awk '{print $1}')

if [[ "$OLD_HASH" == "$NEW_HASH" ]] ; then
echo "Checksums not changed. Will use existing Gradle build..."
else
echo "Checksums have changed. Triggering Gradle build..."
buildConstellation
fi
else
echo "Checksums file does not exist. Triggering Gradle build..."
buildConstellation
fi