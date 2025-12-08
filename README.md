# Moodroid
Android Remote Control Application for Moode Audio Player

## What is Moodroid?

Moodroid is a **remote control app** for [Moode Audio Player](https://moodeaudio.org/) running on Raspberry Pi. It provides a convenient Android interface to control your Moode audio system.

**Important**: This app does NOT play audio on your Android device. It acts as a remote control for your Moode Audio Player (Raspberry Pi), which outputs audio through its connected DAC/amplifier/speakers.

```
Internet → Raspberry Pi (Moode) → DAC → Amplifier → HiFi Speakers
                ↑
                │ Control via Moodroid App
                │
           Android Device
```

## Features

* WebView wrapper for Moode Audio web interface with performance optimizations:
  - Hardware acceleration enabled for smooth rendering
  - Optimized cache settings for audio streaming stability
  - Safe Browsing enabled for security (Android 8.0+)
  - Wake lock to maintain stable connection to Moode server
* Hardware volume button control (volume up/down buttons adjust Moode volume)
* Connection status indicator in the top bar:
  - Green dot: Connected to Moode server
  - Red dot: Connection failed or lost
  - Gray dot: Connection status unknown (initial state)
* Modern Material3 Settings screen:
  - URL history and favorites (up to 10 recent servers)
  - Quick access to previous Moode servers via dropdown
  - Clear history option
  - About section with feature list
* Adaptive UI for landscape and portrait orientations:
  - Optimized FAB positioning in landscape mode
  - Compact TopAppBar (hides icon to save vertical space)
  - Reduced spacing in Settings for better content visibility
* Floating refresh button for manual page reload
* Configurable volume step size (1-100)
* Input validation for URL and volume settings

## Audio Streaming Recommendations

For best streaming stability on Moode Audio Player:
- **Recommended**: AAC streams (faster startup, more stable)
- **Not recommended**: HLS streams (.m3u8) - slower startup, more complex

Note: The audio format affects playback on your Raspberry Pi, not the Android app.

## Architecture

Moodroid follows **Clean Architecture** principles with modern Android development standards:

### Layer Structure

```
app/
├── domain/                    # Business logic layer
│   ├── model/                # Domain models (Settings, ConnectionState, Result)
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Use cases (business logic operations)
├── data/                     # Data layer
│   ├── source/
│   │   ├── local/           # Local data source (DataStore)
│   │   └── remote/          # Remote data source (OkHttp)
│   ├── MoodeRepositoryImpl  # Moode API implementation
│   └── SettingsRepositoryImpl # Settings storage implementation
├── di/                       # Dependency injection (Hilt modules)
│   ├── AppModule            # Application-level dependencies
│   └── RepositoryModule     # Repository bindings
├── ui/                       # Presentation layer (Jetpack Compose)
│   ├── MainScreen           # Main navigation and top bar
│   ├── WebViewContent       # Moode Audio WebView
│   └── PreferenceComposables # Settings screen
└── viewmodel/               # ViewModels with StateFlow
    └── SettingsViewModel    # Settings and connection management
```

### Key Technologies

- **Clean Architecture**: Separation of concerns with domain, data, and presentation layers
- **Dependency Injection**: Hilt for compile-time DI and better testability
- **Reactive State**: StateFlow instead of LiveData for modern Kotlin-first approach
- **Coroutines**: Structured concurrency with proper IO dispatcher usage
- **Jetpack Compose**: Modern declarative UI with Material3
- **Repository Pattern**: Abstraction layer between data sources and business logic
- **Use Cases**: Single responsibility for each business operation
- **Type-Safe Error Handling**: Result sealed class for Success/Error/Loading states

### Benefits

- ✅ **Testability**: Each layer can be tested independently
- ✅ **Maintainability**: Clear separation of concerns
- ✅ **Scalability**: Easy to add new features without affecting existing code
- ✅ **Type Safety**: Compile-time checks with sealed classes and StateFlow
- ✅ **Modern Standards**: Follows Google's recommended architecture guidelines

## Installation Guide

### Method 1: Download Pre-built APK (Recommended)

**Easiest way to install Moodroid:**

1. Go to the [Releases page](https://github.com/harzoglups/MoodeAudio/releases)
2. Download the latest `Moodroid-vX.X.X.apk` file
3. Transfer the APK to your Android device
4. Open the APK file on your device and follow the installation prompts
5. You may need to enable "Install from Unknown Sources" in your device settings

**Note**: If your device warns about installing from unknown sources, this is normal for APKs not downloaded from the Play Store.

### Method 2: Build from Source

If you prefer to build the app yourself:

#### Step 1: Download the Project

Clone the repository using Git:
```bash
git clone https://github.com/harzoglups/MoodeAudio.git
```

Alternatively, download the project as a ZIP file:

* Click on the green "Code" button.
* Select "Download ZIP".

#### Step 2: Enable Developer Options on Your Phone

1. Go to Settings.
2. Navigate to About my phone.
3. Tap on Build number multiple times until Developer Options are enabled.

#### Step 3: Enable USB Debugging

1. Go to Settings.
2. Select System.
3. Navigate to Developer options.
4. Activate USB Debugging.

#### Step 4: Download and Install Android Studio

Download Android Studio from [here](https://developer.android.com/studio)

#### Step 5: Install the Application on Your Phone

1. Open the project in Android Studio.
2. Click the "Play" button or navigate to Run > Run 'app' from the menu.

## Usage

1. Launch Moodroid on your Android device
2. On first launch, go to Settings (⚙️ icon in the top bar)
3. Enter your Moode Audio server URL (e.g., `http://192.168.1.100` or `http://moode.local`)
4. Optionally adjust the volume step size
5. Navigate back to see your Moode Audio interface
6. Use your device's hardware volume buttons to control Moode volume

## Known Issues

* The URL `http://moode.local` might not always work. Setting the IP address directly in the settings screen (e.g., `http://xxx.xxx.xxx.xxx`) tends to yield better results.
