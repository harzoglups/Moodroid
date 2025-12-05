package com.moode.android.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moode.android.R
import com.moode.android.domain.model.ConnectionState
import com.moode.android.viewmodel.SettingsViewModel

enum class MoodroidScreens(@StringRes val title: Int) {
    MAIN(R.string.main_view_title),
    SETTINGS(R.string.settings_views_title)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = MoodroidScreens.valueOf(
        backStackEntry?.destination?.route ?: MoodroidScreens.MAIN.name
    )
    Scaffold(
        topBar = {
            MoodroidTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                action = { navController.navigate(MoodroidScreens.SETTINGS.name) },
                settingsViewModel = settingsViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MoodroidScreens.MAIN.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(MoodroidScreens.MAIN.name) {
                WebViewContent(settingsViewModel = settingsViewModel)
            }
            composable(MoodroidScreens.SETTINGS.name) {
                PreferenceScreen(settingsViewModel = settingsViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodroidTopBar(
    currentScreen: MoodroidScreens,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    action: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val connectionStatus by settingsViewModel.connectionStatus.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Hide icon in landscape to save space
                if (!isLandscape) {
                    Image(
                        painter = painterResource(id = R.drawable.img),
                        contentDescription = stringResource(R.string.main_app_icon_description),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(stringResource(currentScreen.title))
                Spacer(modifier = Modifier.width(8.dp))
                // Connection status indicator
                val statusColor = when (connectionStatus) {
                    ConnectionState.CONNECTED -> Color(0xFF4CAF50) // Green
                    ConnectionState.DISCONNECTED -> Color(0xFFF44336) // Red
                    ConnectionState.UNKNOWN -> Color(0xFF9E9E9E) // Gray
                }
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
            }
        },
        actions = {
            if (currentScreen == MoodroidScreens.MAIN) {
                IconButton(
                    onClick = {
                        action()
                    },
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.main_settings_button))
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
