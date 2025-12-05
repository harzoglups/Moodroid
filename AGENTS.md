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
4. Wait for user feedback before proceeding

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
