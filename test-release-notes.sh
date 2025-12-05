#!/bin/bash

# Test script to simulate GitHub Actions release notes generation locally

set -e

echo "🧪 Testing release notes generation..."
echo ""

# Get latest tag
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "v0.0.0")
echo "📌 Latest tag: $LATEST_TAG"

# Get commits since last tag
if [ "$LATEST_TAG" == "v0.0.0" ]; then
  COMMITS=$(git log --pretty=format:"- %s" --no-merges)
else
  COMMITS=$(git log ${LATEST_TAG}..HEAD --pretty=format:"- %s" --no-merges)
fi

echo "📝 Commits to include:"
echo "$COMMITS"
echo ""

# Check if there are any commits
if [ -z "$COMMITS" ]; then
  echo "❌ No new commits since last tag"
  exit 0
fi

# Determine bump type based on conventional commits
BUMP_TYPE="patch"

# Check for breaking changes (major bump)
if echo "$COMMITS" | grep -qiE "^- (feat|fix|chore|refactor|docs)(\(.+\))?!:|BREAKING CHANGE:|breaking:|^- break:"; then
  BUMP_TYPE="major"
  echo "🚨 Breaking changes detected → MAJOR version bump"
# Check for features (minor bump)
elif echo "$COMMITS" | grep -qE "^- feat(\(.+\))?:"; then
  BUMP_TYPE="minor"
  echo "✨ Features detected → MINOR version bump"
# Check for fixes, docs, refactor, chore (patch bump)
elif echo "$COMMITS" | grep -qE "^- (fix|docs|refactor|chore|style|test|perf)(\(.+\))?:"; then
  BUMP_TYPE="patch"
  echo "🐛 Fixes/improvements detected → PATCH version bump"
else
  echo "⚠️ No conventional commits detected, defaulting to PATCH bump"
  BUMP_TYPE="patch"
fi

# Calculate next version
VERSION=${LATEST_TAG#v}
IFS='.' read -ra VERSION_PARTS <<< "$VERSION"
MAJOR=${VERSION_PARTS[0]:-0}
MINOR=${VERSION_PARTS[1]:-0}
PATCH=${VERSION_PARTS[2]:-0}

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
echo "📦 Next version: $NEW_VERSION (${BUMP_TYPE} bump)"
echo ""

# Categorize commits
BREAKING=$(echo "$COMMITS" | grep -iE "^- (feat|fix|chore|refactor|docs)(\(.+\))?!:|BREAKING CHANGE:|breaking:" || true)
FEATURES=$(echo "$COMMITS" | grep "^- feat" || true)
FIXES=$(echo "$COMMITS" | grep "^- fix" || true)
DOCS=$(echo "$COMMITS" | grep "^- docs" || true)
REFACTOR=$(echo "$COMMITS" | grep "^- refactor" || true)
PERF=$(echo "$COMMITS" | grep "^- perf" || true)
CHORE=$(echo "$COMMITS" | grep "^- chore" || true)
OTHER=$(echo "$COMMITS" | grep -v "^- feat\|^- fix\|^- docs\|^- refactor\|^- perf\|^- chore" || true)

# Build release notes
echo "## What's Changed" > release_notes_test.md
echo "" >> release_notes_test.md
echo "**Version bump**: ${BUMP_TYPE} (${LATEST_TAG} → ${NEW_VERSION})" >> release_notes_test.md
echo "" >> release_notes_test.md

if [ -n "$BREAKING" ]; then
  echo "### ⚠️ BREAKING CHANGES" >> release_notes_test.md
  echo "$BREAKING" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$FEATURES" ]; then
  echo "### ✨ Features" >> release_notes_test.md
  echo "$FEATURES" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$FIXES" ]; then
  echo "### 🐛 Bug Fixes" >> release_notes_test.md
  echo "$FIXES" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$PERF" ]; then
  echo "### ⚡ Performance" >> release_notes_test.md
  echo "$PERF" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$REFACTOR" ]; then
  echo "### ♻️ Refactoring" >> release_notes_test.md
  echo "$REFACTOR" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$DOCS" ]; then
  echo "### 📚 Documentation" >> release_notes_test.md
  echo "$DOCS" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$CHORE" ]; then
  echo "### 🔧 Chores" >> release_notes_test.md
  echo "$CHORE" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

if [ -n "$OTHER" ]; then
  echo "### Other Changes" >> release_notes_test.md
  echo "$OTHER" >> release_notes_test.md
  echo "" >> release_notes_test.md
fi

echo "✅ Release notes generated successfully!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📄 RELEASE NOTES PREVIEW:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
cat release_notes_test.md
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📁 Full content saved to: release_notes_test.md"
