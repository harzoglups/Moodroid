#!/bin/bash

# Script to create a new release tag based on conventional commits
# Usage: ./create-release.sh [major|minor|patch]

set -e

# Get the bump type (default to minor)
BUMP_TYPE=${1:-minor}

if [[ ! "$BUMP_TYPE" =~ ^(major|minor|patch)$ ]]; then
  echo "Error: Invalid bump type. Use 'major', 'minor', or 'patch'"
  exit 1
fi

# Make sure we're on main branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "main" ]; then
  echo "Error: You must be on the 'main' branch to create a release"
  echo "Current branch: $CURRENT_BRANCH"
  exit 1
fi

# Make sure working directory is clean
if [ -n "$(git status --porcelain)" ]; then
  echo "Error: Working directory is not clean. Commit or stash your changes first."
  exit 1
fi

# Get the latest tag
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "v0.0.0")
echo "Latest tag: $LATEST_TAG"

# Parse version
VERSION=${LATEST_TAG#v}
IFS='.' read -ra VERSION_PARTS <<< "$VERSION"
MAJOR=${VERSION_PARTS[0]:-0}
MINOR=${VERSION_PARTS[1]:-0}
PATCH=${VERSION_PARTS[2]:-0}

# Bump version
case "$BUMP_TYPE" in
  major)
    MAJOR=$((MAJOR + 1))
    MINOR=0
    PATCH=0
    ;;
  minor)
    MINOR=$((MINOR + 1))
    PATCH=0
    ;;
  patch)
    PATCH=$((PATCH + 1))
    ;;
esac

NEW_VERSION="v${MAJOR}.${MINOR}.${PATCH}"
echo "New version: $NEW_VERSION"

# Update version in build.gradle.kts
echo "Updating version in app/build.gradle.kts..."
./update-version.sh "$NEW_VERSION"

# Get commits since last tag for preview
echo -e "\n📝 Commits since $LATEST_TAG:"
if [ "$LATEST_TAG" == "v0.0.0" ]; then
  git log --pretty=format:"  - %s" --no-merges | head -20
else
  git log ${LATEST_TAG}..HEAD --pretty=format:"  - %s" --no-merges
fi

echo -e "\n"
read -p "Create tag $NEW_VERSION and commit version changes? (y/N) " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
  # Commit version changes
  git add app/build.gradle.kts
  git commit -m "chore(release): bump version to $NEW_VERSION"
  
  # Create annotated tag with automatic message
  git tag -a "$NEW_VERSION" -m "Release $NEW_VERSION"
  echo "✅ Version committed and tag $NEW_VERSION created locally"
  echo ""
  echo "To push the changes and tag to trigger the release workflow, run:"
  echo "  git push origin main"
  echo "  git push origin $NEW_VERSION"
else
  echo "❌ Cancelled"
  # Revert version changes
  git checkout app/build.gradle.kts
  exit 1
fi
