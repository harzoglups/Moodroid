#!/bin/bash

# Script to update versionName and versionCode in app/build.gradle.kts
# Usage: ./update-version.sh <version>
# Example: ./update-version.sh 1.2.0

set -e

if [ -z "$1" ]; then
  echo "Error: Version number is required"
  echo "Usage: ./update-version.sh <version>"
  echo "Example: ./update-version.sh 1.2.0"
  exit 1
fi

VERSION=$1
# Remove 'v' prefix if present
VERSION=${VERSION#v}

BUILD_GRADLE="app/build.gradle.kts"

if [ ! -f "$BUILD_GRADLE" ]; then
  echo "Error: $BUILD_GRADLE not found"
  exit 1
fi

# Get current versionCode and increment it
CURRENT_VERSION_CODE=$(grep "versionCode = " "$BUILD_GRADLE" | sed 's/.*versionCode = \([0-9]*\).*/\1/')
NEW_VERSION_CODE=$((CURRENT_VERSION_CODE + 1))

echo "Updating version in $BUILD_GRADLE"
echo "  versionName: $VERSION"
echo "  versionCode: $CURRENT_VERSION_CODE → $NEW_VERSION_CODE"

# Update versionName
sed -i.bak "s/versionName = \".*\"/versionName = \"$VERSION\"/" "$BUILD_GRADLE"

# Update versionCode
sed -i.bak "s/versionCode = [0-9]*/versionCode = $NEW_VERSION_CODE/" "$BUILD_GRADLE"

# Remove backup file
rm -f "${BUILD_GRADLE}.bak"

echo "✅ Version updated successfully"
