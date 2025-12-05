# Release Guide

## Automatic Version Management

The release system now **automatically updates** the `versionName` and `versionCode` in `app/build.gradle.kts` based on the release tag. You no longer need to manually edit version numbers!

### How it Works

1. **Auto Release workflow**: Analyzes commits, calculates next version, updates `build.gradle.kts`, builds APK, creates release
2. **Manual tag release**: When you push a tag (e.g., `v1.2.0`), it updates `build.gradle.kts` to match the tag
3. **Local script**: `create-release.sh` now updates the version and commits it before creating the tag

**Version in Settings**: The app will display the correct version automatically based on `versionName` in `build.gradle.kts`!

## Quick Start: Creating Your First Release

### Option 1: Auto Release (Fully Automated - Recommended)

1. **Merge your changes to `main`**:
   ```bash
   git checkout main
   git push origin main
   ```

2. **Trigger the Auto Release**:
   - Go to your repository on GitHub: https://github.com/harzoglups/MoodeAudio
   - Click on **Actions** tab
   - Select **"Auto Release"** workflow on the left
   - Click **"Run workflow"** button (top right)

3. **The workflow automatically**:
   - Analyzes your commits to determine the version bump (major/minor/patch)
   - Calculates the next version number
   - Updates `versionName` and `versionCode` in `build.gradle.kts`
   - Builds the APK with the correct version
   - Creates a GitHub release with categorized release notes
   - Creates a git tag

4. **Check your release**:
   - Go to **Releases** section on GitHub
   - You should see your new release with:
     - Version tag (e.g., `v1.0.0`)
     - Organized release notes by category
     - APK file ready to download (Moodroid-v1.0.0.apk)

### Option 2: Using Local Script

1. **Prepare your main branch**:
   ```bash
   git checkout main
   git push origin main
   ```

2. **Create the release**:
   ```bash
   ./create-release.sh minor
   ```
   
   This will:
   - Show you the commits since last release
   - Calculate the next version
   - **Update `versionName` and `versionCode` in `build.gradle.kts`**
   - Commit the version changes
   - Create the git tag
   - Ask for confirmation

3. **Push the changes and tag**:
   ```bash
   git push origin main
   git push origin v1.0.0  # Use the tag that was created
   ```

4. **The GitHub Action will automatically**:
   - Build the APK (with the version already updated)
   - Create the release
   - Generate release notes

### Option 3: Manual Tag (Advanced)

If you prefer to create tags manually:

1. **Create and push a tag**:
   ```bash
   git tag -a v1.2.0 -m "Release v1.2.0"
   git push origin v1.2.0
   ```

2. **The workflow will**:
   - Extract the version from the tag (e.g., `1.2.0` from `v1.2.0`)
   - Update `versionName` and `versionCode` in `build.gradle.kts`
   - Build the APK
   - Create the release

## For Your v1.0.0 Release

Since the project is feature-complete, here's how to create v1.0.0:

**Using Auto Release (Recommended):**
```bash
# Make sure main is up to date
git checkout main
git push origin main

# Go to GitHub Actions and run "Auto Release" workflow
# It will automatically detect this is the first release and create v1.0.0
```

**Or using the script:**
```bash
# Make sure main is up to date
git checkout main
git push origin main

# Create release (will update version, commit, and create tag)
./create-release.sh minor
git push origin main
git push origin v1.0.0
```

## Understanding the Version System

### versionName (User-Facing)
- Displayed in the app's About section (Settings)
- Follows [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`
- **MAJOR** (X.0.0): Breaking changes
- **MINOR** (0.X.0): New features, backward compatible
- **PATCH** (0.0.X): Bug fixes

### versionCode (Internal)
- Used by Android to manage updates
- Automatically incremented with each release
- Users never see this number

### How Version Bumps Are Determined

The Auto Release workflow analyzes your commit messages:

- **MAJOR bump** (1.0.0 → 2.0.0): Commits with `BREAKING CHANGE:`, `feat!:`, or `fix!:`
- **MINOR bump** (1.0.0 → 1.1.0): Commits with `feat:` prefix
- **PATCH bump** (1.0.0 → 1.0.1): Commits with `fix:`, `docs:`, `refactor:`, `chore:`, `perf:`

Example commits:
```bash
feat(settings): add dark mode support        # MINOR bump
fix(webview): resolve white screen issue     # PATCH bump
feat!: redesign settings UI                  # MAJOR bump (breaking change)
```

## Release Notes Format

The workflow automatically categorizes commits:

- ✨ **Features** - `feat:` commits
- 🐛 **Bug Fixes** - `fix:` commits
- ⚡ **Performance** - `perf:` commits
- ♻️ **Refactoring** - `refactor:` commits
- 📚 **Documentation** - `docs:` commits
- 🔧 **Chores** - `chore:` commits

Example release notes generated:

```markdown
## What's Changed

**Version bump**: minor (v0.0.0 → v1.0.0)

### ✨ Features
- feat(settings): add modern Material3 UI with URL history and about section
- feat(ui): optimize layout for landscape orientation

### ⚡ Performance
- perf(webview): optimize rendering and caching for faster page loads

### 📚 Documentation
- docs(todo): mark project as feature-complete and cancel unsuitable features
```

## Troubleshooting

### "No tags found" on first release
- Normal! The workflow will create v1.0.0 as the first release

### APK not attached to release
- Check the Actions tab for build errors
- Make sure the workflow completed successfully
- Look for Java/Gradle build errors in the logs

### Version not updated in app
- The workflows automatically update `versionName` in `build.gradle.kts`
- If you see the wrong version, check that the APK was built AFTER the version update step

### Release notes are empty
- Make sure you have commits since the last tag
- Use conventional commit format for better categorization

### Build fails with Java errors
- The workflow uses JDK 17 by default
- Check the gradle-wrapper.properties and build.gradle.kts for Java version compatibility

## Best Practices

1. **Use Conventional Commits**: Always use the format `type(scope): description`
   - Makes release notes more organized
   - Automatically determines correct version bump

2. **Test Before Release**: Build and test locally before creating a release
   ```bash
   export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
   ./gradlew assembleRelease
   ```

3. **Keep main branch stable**: Only merge tested, working code to main

4. **Create releases from main**: Always create releases from the main branch

5. **Review release notes**: Check the generated release notes in the Actions tab before sharing

## Next Steps

After creating v1.0.0:
1. Download the APK from the release page
2. Test installation on a device
3. Share the release link with users!

**Release URL**: https://github.com/harzoglups/MoodeAudio/releases
