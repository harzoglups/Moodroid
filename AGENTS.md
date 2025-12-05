# Agent Guidelines for Moodroid Android Project

## Git Commit and Push Policy
**CRITICAL**: NEVER create git commits or push to remote unless explicitly requested by the user.
- NEVER commit without explicit authorization from the user
- NEVER push to remote without explicit authorization from the user
- Build and install the app for testing FIRST
- Wait for user confirmation that the feature works correctly
- Only commit after explicit approval: "make a commit", "commit this", "create a commit", etc.
- Only push after explicit approval: "push this", "push to remote", etc.
- If you commit or push prematurely, you MUST apologize and offer to amend or revert

## Commit Message Format
**REQUIRED**: All commit messages MUST follow Conventional Commits specification.
- Format: `<type>(<scope>): <description>`
- Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- Example: `feat(map): add floating layer selection button`
- Example: `fix(markers): improve touch detection for marker deletion`
- Example: `docs(readme): update map interaction instructions`
- Keep description concise, use imperative mood ("add" not "added")
- **NEVER** create commit messages that don't follow this format

## Language Policy
**IMPORTANT**: All code, documentation, and commit messages MUST be in English, even if the conversation is in French.
- **Code**: Function names, variable names, class names, comments - all in English
- **Documentation**: All .md files must be in English (README.md, DEVELOPMENT.md, TODO.md, etc.)
- **Commit messages**: Always in English
- **Conversation**: Can be in French, but all written artifacts must be in English

## Build & Test Commands
- Build: `./gradlew build` or `./gradlew assembleDebug`
- Install: `./gradlew installDebug`
- Launch app: `~/Library/Android/sdk/platform-tools/adb shell am start -n com.moode.android/.MainActivity`
- Run tests: `./gradlew test` (unit tests) or `./gradlew connectedAndroidTest` (instrumented)
- Run single test: `./gradlew test --tests com.moode.android.ExampleUnitTest.addition_isCorrect`
- Lint: `./gradlew lint`
- Clean: `./gradlew clean`
- **IMPORTANT**: Set JAVA_HOME before running Gradle: `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`

## Development Workflow
**REQUIRED**: After every code change, you MUST:
1. Build the application: `./gradlew assembleDebug`
2. Install it on the connected device: `./gradlew installDebug`
3. Launch the application: `~/Library/Android/sdk/platform-tools/adb shell am start -n com.moode.android/.MainActivity`
4. Update relevant documentation (README.md, etc.) to reflect code changes, bug fixes, or new features
5. Wait for user feedback before proceeding

## Code Style
- **Language**: Kotlin 1.9.10, JVM target 1.8
- **Style Guide**: Kotlin Official style (configured in `.idea/codeStyles/Project.xml`)
- **Package Structure**: `com.moode.android.{data|ui|viewmodel}`
- **Imports**: Group by package, Android/AndroidX first, then third-party, then project imports
- **Naming**: camelCase for variables/functions, PascalCase for classes, UPPER_SNAKE_CASE for constants in companion objects
- **Types**: Use explicit types for public APIs, inference for local variables; prefer `val` over `var`
- **Compose**: Use `@Composable` functions for UI, Material3 components, modifier chaining with proper formatting
- **ViewModels**: Extend `AndroidViewModel`, expose `LiveData`/`Flow` for state, use `viewModelScope` for coroutines
- **Error Handling**: Use try-catch blocks, log with `Log.i/e(TAG, message)`, `TAG` as companion object constant
- **Async**: Use coroutines with `viewModelScope`/`lifecycleScope`, avoid blocking calls on main thread
- **DataStore**: Use Preferences DataStore for key-value storage, expose as `Flow` and convert to `LiveData` with `asLiveData()`

## Release Management
**IMPORTANT**: This project uses automated release workflows for version management.

### Creating Releases
**Three methods available** (see `RELEASE_GUIDE.md` for details):

1. **Auto Release (Recommended)**: Trigger GitHub Action manually
   - Go to GitHub Actions → "Auto Release" → "Run workflow"
   - Automatically analyzes commits, determines version bump, builds APK, creates release
   - No manual version editing required

2. **Local Script**: Use `./create-release.sh [major|minor|patch]`
   - Calculates next version, updates `build.gradle.kts`, commits changes, creates tag
   - Push tag to trigger automated build and release

3. **Manual Tag**: Create and push a tag (e.g., `git tag -a v1.2.0 -m "Release v1.2.0"`)
   - GitHub Actions automatically builds and creates release

### Version Management
- **NEVER manually edit** `versionName` or `versionCode` in `app/build.gradle.kts`
- The release system automatically updates these values
- `versionCode` is auto-incremented with each release
- `versionName` follows semantic versioning (MAJOR.MINOR.PATCH)

### Conventional Commits for Releases
Commit message format determines version bumps:
- `feat:` → MINOR version bump (new features)
- `fix:`, `docs:`, `perf:`, `refactor:`, `chore:` → PATCH version bump
- `feat!:`, `fix!:`, `BREAKING CHANGE:` → MAJOR version bump (breaking changes)

### Release Workflow Files
- `.github/workflows/auto-release.yml` - Fully automated release workflow
- `.github/workflows/release-on-tag.yml` - Triggered by tag push
- `update-version.sh` - Script to update version in build.gradle.kts
- `create-release.sh` - Local script for creating releases
- `RELEASE_GUIDE.md` - Complete release documentation

