package com.moode.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark color scheme inspired by Moode Audio's elegant dark interface
 */
private val MoodeDarkColorScheme = darkColorScheme(
    primary = MoodeBlue,                    // Top bar, buttons
    onPrimary = Color.White,                // Text on primary
    primaryContainer = MoodeDarkGray,       // Top bar background
    onPrimaryContainer = Color.White,       // Text in top bar (pure white for max contrast)
    
    secondary = MoodeTurquoise,             // Accents, FAB
    onSecondary = Color.White,              // Text on secondary
    secondaryContainer = MoodeMediumGray,   // FAB container
    onSecondaryContainer = Color.White,     // Text on FAB
    
    tertiary = MoodeSkyBlue,                // Additional accents
    onTertiary = Color.White,
    
    background = Color(0xFF1A1A1A),         // Screen background (very dark)
    onBackground = Color.White,             // Text on background (pure white)
    
    surface = MoodeDarkGray,                // Cards, surfaces
    onSurface = Color.White,                // Text on surfaces (pure white)
    surfaceVariant = MoodeMediumGray,       // Settings cards
    onSurfaceVariant = Color.White,         // Text on cards (pure white for better readability)
    
    outline = MoodeLightGray,               // Borders
    outlineVariant = MoodeLightGray.copy(alpha = 0.3f)
)

/**
 * Light color scheme with professional, clean look
 */
private val MoodeLightColorScheme = lightColorScheme(
    primary = MoodeLightBlue,               // Top bar, buttons
    onPrimary = Color.White,                // Text on primary
    primaryContainer = MoodeLightBlue.copy(alpha = 0.15f), // Top bar background (subtle)
    onPrimaryContainer = MoodeNavy,         // Text in top bar
    
    secondary = MoodeTurquoise,             // Accents, FAB
    onSecondary = Color.White,              // Text on secondary
    secondaryContainer = MoodeSkyBlue.copy(alpha = 0.15f), // FAB container
    onSecondaryContainer = MoodeNavy,       // Text on FAB
    
    tertiary = MoodeDeepBlue,               // Text fields, additional accents
    onTertiary = Color.White,
    
    background = MoodePaleGray,             // Screen background
    onBackground = MoodeDarkText,           // Text on background
    
    surface = Color.White,                  // Cards, surfaces
    onSurface = MoodeDarkText,              // Text on surfaces
    surfaceVariant = MoodeSurfaceGray,      // Settings cards
    onSurfaceVariant = MoodeDarkText,       // Text on cards
    
    outline = MoodeBorderGray,              // Borders
    outlineVariant = MoodeBorderGray.copy(alpha = 0.5f)
)

@Composable
fun MoodeAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled by default to maintain Moode Audio branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> MoodeDarkColorScheme
        else -> MoodeLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}