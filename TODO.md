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
- **Status**: Pending

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
- **Status**: Pending

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
- **Status**: Pending

### 13. Dynamic Shortcuts
**Priority: Low**
- **Current**: No app shortcuts
- **Issue**: No quick actions from launcher
- **Improvement**: Add App Shortcuts for quick actions (play/pause, next, previous)
- **Status**: Pending

## Priority Summary

### High Priority
1. User Input Validation (prevents crashes)
2. Network Error Handling with user feedback
3. Connection Status Indicator

### Medium Priority
4. OkHttpClient Lifecycle Management
5. Swipe to Refresh
6. HTTPS Certificate Handling
7. Offline Mode Detection
8. Android Media Controls Integration

### Low Priority
9. WebView Performance optimization
10. URL Favorites/History
11. Landscape Orientation optimization
12. Android Widget
13. Dynamic Shortcuts

## Completed
- ✅ Fix WebView white screen issue
- ✅ Network Error Handling (coroutines + Toast feedback)
- ✅ OkHttpClient Lifecycle Management
- ✅ User Input Validation (URL and Volume Step with error messages)
- ✅ Connection Status Indicator (colored dot in top bar)
- ✅ WebView Performance Optimization (hardware acceleration, intelligent caching, Safe Browsing)
- ✅ URL Favorites/History (modern Material3 Settings UI with history dropdown and about section)
