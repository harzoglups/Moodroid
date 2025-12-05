package com.moode.android.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moode.android.R
import kotlinx.coroutines.delay

/**
 * Splash screen with music-inspired animation
 * Features:
 * - App icon with dramatic zoom animation
 * - Pulsating sound waves emanating from icon
 * - Gradient background matching Moode Audio theme
 * - Smooth transition after animation completes
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation state
    var animationStarted by remember { mutableStateOf(false) }
    
    // Use Transition API for more control
    val transition = updateTransition(
        targetState = animationStarted,
        label = "splashTransition"
    )
    
    // Icon scale with dramatic zoom from 0 to 1
    val iconScale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "iconScale"
    ) { started ->
        if (started) 1f else 0.0f
    }
    
    // Icon alpha fade in
    val iconAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 1000, easing = LinearEasing)
        },
        label = "iconAlpha"
    ) { started ->
        if (started) 1f else 0f
    }
    
    // Start animation after composition
    LaunchedEffect(Unit) {
        delay(200) // Give time for initial state to render
        animationStarted = true
        delay(2800) // Total splash duration
        onSplashFinished()
    }
    
    // Sound wave animation (infinite pulsating circles)
    val infiniteTransition = rememberInfiniteTransition(label = "soundWaves")
    
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1Alpha"
    )
    
    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(500)
        ),
        label = "wave2"
    )
    
    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(500)
        ),
        label = "wave2Alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Sound wave circles (behind icon)
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Wave 1
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(wave1Scale)
                    .alpha(wave1Alpha)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            
            // Wave 2
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(wave2Scale)
                    .alpha(wave2Alpha)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        
        // App icon with dramatic zoom
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = stringResource(R.string.splash_logo_description),
                modifier = Modifier
                    .size(120.dp)
                    .scale(iconScale)
                    .alpha(iconAlpha)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name with fade-in
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(iconAlpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = stringResource(R.string.splash_tagline),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.alpha(iconAlpha)
            )
        }
    }
}
