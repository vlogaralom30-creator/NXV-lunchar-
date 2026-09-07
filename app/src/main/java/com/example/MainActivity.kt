package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

enum class LauncherScreen {
    HOME,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private var currentScreen by mutableStateOf(LauncherScreen.HOME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Crossfade(
                    targetState = currentScreen,
                    animationSpec = tween(durationMillis = 250),
                    label = "screen_transition",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        LauncherScreen.HOME -> {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onOpenSettings = { currentScreen = LauncherScreen.SETTINGS }
                            )
                        }
                        LauncherScreen.SETTINGS -> {
                            val themeConfig by homeViewModel.themeConfig.collectAsStateWithLifecycle()
                            BackHandler {
                                currentScreen = LauncherScreen.HOME
                            }
                            SettingsScreen(
                                themeConfig = themeConfig,
                                onUpdateThemeConfig = { updateBlock ->
                                    homeViewModel.updateThemeConfig(updateBlock)
                                },
                                onBack = { currentScreen = LauncherScreen.HOME }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Whenever home intent or home button is pressed, return to HOME screen
        if (intent.action == android.content.Intent.ACTION_MAIN &&
            intent.hasCategory(android.content.Intent.CATEGORY_HOME)
        ) {
            currentScreen = LauncherScreen.HOME
        }
    }
}
