# Moodroid
Android Application Wrapper for Moode Audio UI with Volume Button Control

## Features

* WebView wrapper for Moode Audio web interface
* Hardware volume button control (volume up/down buttons adjust Moode volume)
* Connection status indicator in the top bar:
  - Green dot: Connected to Moode server
  - Red dot: Connection failed or lost
  - Gray dot: Connection status unknown (initial state)
* Configurable volume step size (1-100)
* Input validation for URL and volume settings

## Installation Guide

### Step 1: Download the Project

Clone the repository using Git:
```bash
git clone https://github.com/harzoglups/Moodroid.git
```

Alternatively, download the project as a ZIP file:

* Click on the green "Code" button.
* Select "Download ZIP".

### Step 2: Enable Developer Options on Your Phone

1. Go to Settings.
2. Navigate to About my phone.
3. Tap on Build number multiple times until Developer Options are enabled.

### Step 3: Enable USB Debugging

1. Go to Settings.
2. Select System.
3. Navigate to Developer options.
4. Activate USB Debugging.

### Step 4: Download and Install Android Studio

Download Android Studio from [here](https://developer.android.com/studio)

### Step 5: Install the Application on Your Phone

1. Open the project in Android Studio.
2. Click the "Play" button or navigate to Run > Run 'app' from the menu.

## Known Issues

* The URL `http://moode.local` might not always work. Setting the IP address directly in the settings screen (e.g., `http://xxx.xxx.xxx.xxx`) tends to yield better results.
