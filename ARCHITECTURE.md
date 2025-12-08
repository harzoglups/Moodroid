# Moodroid Architecture

This document describes the architecture of the Moodroid Android application following Clean Architecture principles.

## Overview

Moodroid is built using **Clean Architecture** with clear separation between domain, data, and presentation layers. The architecture follows modern Android development best practices with Hilt for dependency injection, Kotlin Coroutines for asynchronous operations, and Jetpack Compose for UI.

## System Architecture

**IMPORTANT**: Moodroid is a **remote control application** for Moode Audio Player. It does NOT play audio locally on the Android device.

```
┌──────────────────────────────────────────────────────────────┐
│                    Physical Architecture                      │
│                                                               │
│  Internet (Radio streams)                                     │
│      ↓                                                        │
│  Raspberry Pi (Moode Audio Player)                           │
│      ↓                                                        │
│  DAC → Amplifier → HiFi Speakers                             │
│      ↑                                                        │
│      │ HTTP/WebSocket (control commands)                     │
│      │                                                        │
│  Android Device (Moodroid App)                               │
│      - WebView displays Moode web interface                  │
│      - Captures volume buttons                               │
│      - Sends control commands to Raspberry Pi                │
│      - Does NOT play audio locally                           │
│                                                               │
│  Can also be controlled from:                                │
│      - PC (any web browser)                                  │
│      - Other phones (any web browser)                        │
│      - Any device with network access                        │
└──────────────────────────────────────────────────────────────┘
```

### Audio Streaming Optimizations

The app includes wake lock and WebView optimizations for audio streaming stability:

- **Wake Lock**: Prevents Android from putting WiFi/CPU to sleep
- **Purpose**: Maintains stable HTTP/WebSocket connection to Moode server
- **Impact**: Keeps the web interface responsive and connection indicator accurate
- **Note**: These optimizations affect the control interface stability, NOT the audio playback itself (which happens on the Raspberry Pi)

### Audio Format Considerations

- **AAC streams**: Recommended for faster startup and better stability on Moode
- **HLS streams (.m3u8)**: More complex segmented format, may cause slower startup
- The audio format affects the **Raspberry Pi playback**, not the Android app

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐    │
│  │   Activity  │  │  Composables│  │   ViewModel      │    │
│  │             │──│             │──│  (StateFlow)     │    │
│  └─────────────┘  └─────────────┘  └──────────────────┘    │
│                                              │               │
└──────────────────────────────────────────────┼───────────────┘
                                               │
┌──────────────────────────────────────────────┼───────────────┐
│                     Domain Layer             │               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                    Use Cases                           │ │
│  │  • GetSettingsUseCase                                  │ │
│  │  • UpdateUrlUseCase                                    │ │
│  │  • TestConnectionUseCase                               │ │
│  │  • SendVolumeCommandUseCase                            │ │
│  │  • GetUrlHistoryUseCase / AddUrlToHistoryUseCase      │ │
│  │  • UpdateVolumeStepUseCase / ClearUrlHistoryUseCase   │ │
│  └────────────────────────────────────────────────────────┘ │
│                             │                                │
│  ┌──────────────────────────▼──────────────────────────┐    │
│  │          Repository Interfaces                      │    │
│  │  • SettingsRepository                               │    │
│  │  • MoodeRepository                                  │    │
│  └─────────────────────────────────────────────────────┘    │
│                             │                                │
│  ┌──────────────────────────▼──────────────────────────┐    │
│  │              Domain Models                          │    │
│  │  • Settings                                         │    │
│  │  • ConnectionState                                  │    │
│  │  • Result<T> (sealed class)                         │    │
│  └─────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────┼───────────────┘
                                               │
┌──────────────────────────────────────────────┼───────────────┐
│                      Data Layer              │               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │          Repository Implementations                  │   │
│  │  • SettingsRepositoryImpl                            │   │
│  │  • MoodeRepositoryImpl                               │   │
│  └──────────────────────┬───────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────▼──────────┬─────────────────────┐  │
│  │     Local Data Source           │  Remote Data Source │  │
│  │  • SettingsLocalDataSource      │  • MoodeRemoteDS    │  │
│  │    (DataStore Preferences)      │    (OkHttp)         │  │
│  └─────────────────────────────────┴─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### 1. Presentation Layer

**Location**: `app/src/main/java/com/moode/android/ui/` and `viewmodel/`

#### Components:
- **MainActivity**: Entry point, handles hardware volume buttons
- **Composables**: UI components (MainScreen, WebViewContent, PreferenceComposables)
- **SettingsViewModel**: Manages UI state with StateFlow

#### Responsibilities:
- Render UI using Jetpack Compose
- Handle user interactions
- Observe state from ViewModel
- Display errors via Toast messages

#### Key Features:
- Uses `@HiltViewModel` for dependency injection
- Uses `StateFlow` for reactive state management
- Observes state with `collectAsStateWithLifecycle()`

### 2. Domain Layer

**Location**: `app/src/main/java/com/moode/android/domain/`

#### Components:

##### Use Cases (`domain/usecase/`)
Each use case represents a single business operation:
- `GetSettingsUseCase`: Retrieve user settings
- `UpdateUrlUseCase`: Update Moode server URL
- `UpdateVolumeStepUseCase`: Update volume step size
- `GetUrlHistoryUseCase`: Get URL history
- `AddUrlToHistoryUseCase`: Add URL to history
- `ClearUrlHistoryUseCase`: Clear URL history
- `TestConnectionUseCase`: Test connection to Moode server
- `SendVolumeCommandUseCase`: Send volume adjustment command

##### Repository Interfaces (`domain/repository/`)
- `SettingsRepository`: Settings persistence contract
- `MoodeRepository`: Moode server interaction contract

##### Domain Models (`domain/model/`)
- `Settings`: URL and volume step data class
- `ConnectionState`: Enum (UNKNOWN, CONNECTED, DISCONNECTED)
- `Result<T>`: Sealed class (Success, Error, Loading)

#### Responsibilities:
- Define business logic
- Define repository contracts (interfaces)
- Define domain models
- No Android framework dependencies

### 3. Data Layer

**Location**: `app/src/main/java/com/moode/android/data/`

#### Components:

##### Repository Implementations
- `SettingsRepositoryImpl`: Implements SettingsRepository using DataStore
- `MoodeRepositoryImpl`: Implements MoodeRepository using OkHttp

##### Data Sources (`data/source/`)
- **Local**: `SettingsLocalDataSource` - DataStore Preferences for settings
- **Remote**: `MoodeRemoteDataSource` - OkHttp for HTTP requests

#### Responsibilities:
- Implement repository interfaces
- Manage data sources (local and remote)
- Handle data transformations
- Run network operations on IO dispatcher
- Convert exceptions to Result types

### 4. Dependency Injection Layer

**Location**: `app/src/main/java/com/moode/android/di/`

#### Modules:
- `AppModule`: Provides application-level dependencies (Context, OkHttpClient)
- `RepositoryModule`: Binds repository interfaces to implementations

#### Technology:
- Uses **Hilt** for compile-time dependency injection
- `@HiltAndroidApp` on Application class
- `@AndroidEntryPoint` on Activity
- `@HiltViewModel` on ViewModels
- `@Singleton` for app-scoped dependencies

## Data Flow

### Reading Settings
```
UI (Composable)
    ↓ collectAsStateWithLifecycle()
ViewModel (StateFlow)
    ↓ observes
Use Case (GetSettingsUseCase)
    ↓ calls
Repository Interface
    ↓ implements
Repository Implementation
    ↓ uses
Local Data Source (DataStore)
```

### Sending Volume Command
```
User presses volume button
    ↓
MainActivity.onKeyDown()
    ↓
ViewModel.sendVolumeCommand()
    ↓
SendVolumeCommandUseCase
    ↓
MoodeRepository
    ↓
MoodeRepositoryImpl
    ↓
MoodeRemoteDataSource (withContext(IO))
    ↓
OkHttp HTTP request
    ↓
Result<Unit> (Success/Error)
    ↓ updates
ConnectionState in ViewModel
    ↓ observed by
UI updates connection indicator
```

## Key Design Patterns

### 1. Repository Pattern
Abstracts data sources behind interfaces, allowing easy swapping of implementations.

### 2. Use Case Pattern
Encapsulates single business operations, following Single Responsibility Principle.

### 3. Dependency Injection
Uses Hilt for:
- Constructor injection
- Improved testability
- Reduced boilerplate
- Compile-time validation

### 4. Reactive State Management
Uses StateFlow for:
- Type-safe reactive updates
- Lifecycle-aware collection
- No LiveData boilerplate

### 5. Result Pattern
Sealed class for error handling:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

## Testing Strategy

### Unit Tests
- **Use Cases**: Test business logic in isolation
- **ViewModels**: Test state management with fake repositories
- **Repositories**: Test with fake data sources

### Integration Tests
- **Repository + DataSource**: Test actual DataStore operations
- **Repository + Remote**: Test with mock HTTP server

### UI Tests
- **Composables**: Test with fake ViewModels
- **End-to-End**: Test full user flows

## Benefits of This Architecture

1. **Testability**: Each layer can be tested independently with mocks/fakes
2. **Maintainability**: Clear separation of concerns makes code easier to understand
3. **Scalability**: Easy to add new features without modifying existing code
4. **Flexibility**: Easy to swap implementations (e.g., DataStore → Room)
5. **Type Safety**: Compile-time checks with sealed classes and StateFlow
6. **Modern**: Follows Google's recommended architecture guidelines
7. **Reusability**: Use cases can be reused across different UI components

## Dependencies

### Core
- Kotlin 1.9.10
- Coroutines 1.8.0

### Android
- Jetpack Compose (Material3)
- Lifecycle (ViewModel, StateFlow)
- DataStore Preferences 1.1.1
- Navigation Compose 2.7.7

### Dependency Injection
- Hilt 2.48
- Hilt Navigation Compose 1.2.0

### Network
- OkHttp 4.12.0

### UI
- Compose BOM 2024.06.00
- WebView (AndroidX WebKit 1.11.0)

## Migration Notes

The application was refactored from a basic MVVM structure to Clean Architecture in version 2.0.0. Key changes:

1. **LiveData → StateFlow**: More modern and Kotlin-first
2. **Direct dependencies → Use Cases**: Better separation of concerns
3. **Manual instantiation → Hilt**: Improved DI and testability
4. **Thread/coroutines → Structured concurrency**: Proper IO dispatcher usage
5. **Basic error handling → Result sealed class**: Type-safe error handling

All existing features were preserved during the refactoring.
