# TODO - Moodroid Improvements

This document tracks potential improvements and features for the Moodroid application.

## 🔧 Technical Improvements

### 1. Network Error Handling
**Priority: High**
- **Current**: `sendVolumeCommand` uses basic `thread{}` instead of coroutines (MainActivity.kt:61-74)
- **Issue**: No user feedback on failure
- **Improvement**: Use coroutines + display Toast/Snackbar on error
- **Status**: ✅ Completed

### 2. User Input Validation
**Priority: High**
- **Current**: Volume Step can crash if input is not a valid number (PreferenceComposables.kt:96-97)
- **Issue**: No URL validation, no input sanitization
- **Improvement**: Add validation + error handling for both URL and Volume Step fields
- **Status**: ✅ Completed

### 3. OkHttpClient Lifecycle Management
**Priority: Medium**
- **Current**: HTTP client is never closed (MainActivity.kt:22)
- **Issue**: Resource leak
- **Improvement**: Close client in `onDestroy()`
- **Status**: ✅ Completed

### 4. WebView Performance
**Priority: Low**
- **Current**: No WebView cache management
- **Issue**: Slower page loads
- **Improvement**: Configure cache to improve performance
- **Status**: ✅ Completed

## 🎨 UX/UI Improvements

### 5. Connection Status Indicator
**Priority: High**
- **Current**: No feedback when volume buttons are pressed, no indication if Moode server is reachable
- **Issue**: User doesn't know if commands are working
- **Improvement**: Add connection status indicator + visual/haptic feedback on volume button press
- **Status**: ✅ Completed

### 6. Swipe to Refresh
**Priority: Medium**
- **Current**: Only FAB button for refresh
- **Issue**: Non-standard Android UX pattern
- **Improvement**: Add standard Android swipe-to-refresh gesture
- **Status**: ❌ Not Implemented - Conflicts with WebView scroll gestures, FAB is sufficient

### 7. URL Favorites / History
**Priority: Low**
- **Current**: No management of multiple Moode servers
- **Issue**: Users with multiple servers must manually type URL each time
- **Improvement**: Dropdown with recent/favorite URLs
- **Status**: ✅ Completed

### 8. Landscape Orientation
**Priority: Low**
- **Current**: FAB refresh button may be poorly positioned in landscape
- **Issue**: UI not optimized for landscape
- **Improvement**: Adapt FAB position based on orientation
- **Status**: ✅ Completed

## 🔒 Security & Robustness

### 9. HTTPS Certificate Handling
**Priority: Medium**
- **Current**: No handling of self-signed certificates
- **Issue**: Cannot connect to local servers with custom certificates
- **Improvement**: Allow acceptance of custom certificates for local servers
- **Status**: Pending

### 10. Offline Mode
**Priority: Medium**
- **Current**: No defined behavior when network is unavailable
- **Issue**: Poor user experience when offline
- **Improvement**: Detect network state and inform user
- **Status**: ❌ Cancelled - Network detection insufficient (device needs to be on same local network as Moode server, not just online)

## 📱 Feature Additions

### 11. Android Media Controls
**Priority: Medium**
- **Current**: No integration with system media controls
- **Issue**: Cannot control playback from notification/lock screen
- **Improvement**: Integrate MediaSession API for system-wide controls
- **Status**: Pending

### 12. Android Widget
**Priority: Low**
- **Current**: No widget available
- **Issue**: Must open app for all interactions
- **Improvement**: Create widget for quick access to playback controls
- **Status**: ❌ Cancelled - Not suitable for WebView wrapper application

### 13. Dynamic Shortcuts
**Priority: Low**
- **Current**: No app shortcuts
- **Issue**: No quick actions from launcher
- **Improvement**: Add App Shortcuts for quick actions (play/pause, next, previous)
- **Status**: ❌ Cancelled - Not suitable for WebView wrapper application

## Priority Summary

### ✅ All Essential Features Completed!
The Moodroid application is feature-complete with all high-priority improvements implemented.

### Remaining Optional Features

#### Medium Priority - Optional
- #9: HTTPS Certificate Handling (only needed if using HTTPS with self-signed certificates)
- #11: Android Media Controls Integration (complex, requires MediaSession API and state extraction from WebView)

#### Low Priority - Cancelled
- ❌ #10: Offline Mode Detection (insufficient - requires same local network detection)
- ❌ #12: Android Widget (not suitable for WebView wrapper)
- ❌ #13: Dynamic Shortcuts (not suitable for WebView wrapper)

## ✅ Completed Features (8/8 Essential)
- ✅ Fix WebView white screen issue
- ✅ Network Error Handling (coroutines + Toast feedback)
- ✅ OkHttpClient Lifecycle Management
- ✅ User Input Validation (URL and Volume Step with error messages)
- ✅ Connection Status Indicator (colored dot in top bar)
- ✅ WebView Performance Optimization (hardware acceleration, intelligent caching, Safe Browsing)
- ✅ URL Favorites/History (modern Material3 Settings UI with history dropdown and about section)
- ✅ Landscape Orientation Optimization (adaptive FAB positioning, compact TopAppBar, reduced spacing)

## ❌ Cancelled Features (3)
- ❌ Swipe to Refresh (#6) - Conflicts with WebView scroll gestures, FAB is sufficient
- ❌ Offline Mode Detection (#10) - Network detection insufficient for local network requirement
- ❌ Android Widget (#12) - Not suitable for WebView wrapper application
- ❌ Dynamic Shortcuts (#13) - Not suitable for WebView wrapper application

## 🔮 Optional Future Features (2)
- #9: HTTPS Certificate Handling - Only needed if using HTTPS with self-signed certificates
- #11: Android Media Controls - Complex feature requiring MediaSession API integration

---

**Project Status**: 🎉 **Feature Complete** - All essential functionality implemented and tested!
