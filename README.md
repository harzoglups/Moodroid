# Moodroid
Android Application Wrapper for Moode Audio UI with Volume Button Control

## Features

* WebView wrapper for Moode Audio web interface with performance optimizations:
  - Hardware acceleration enabled for smooth rendering
  - Intelligent caching for faster page loads
  - Safe Browsing enabled for security (Android 8.0+)
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
