package com.still.clock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.still.clock.data.ClockSettings
import com.still.clock.data.ClockSettingsRepository
import com.still.clock.ui.ClockScreen
import com.still.clock.ui.palette
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()

        val repository = ClockSettingsRepository(applicationContext)
        setContent {
            val settings by repository.settings.collectAsStateWithLifecycle(
                initialValue = ClockSettings(),
            )
            val palette = settings.atmosphere.palette
            val scope = rememberCoroutineScope()

            DisposableEffect(settings.keepScreenOn) {
                if (settings.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                onDispose {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = palette.accent,
                    onPrimary = palette.background,
                    background = palette.background,
                    onBackground = palette.primary,
                    surface = palette.background,
                    onSurface = palette.primary,
                ),
            ) {
                ClockScreen(
                    settings = settings,
                    onSettingsChange = { updated ->
                        scope.launch { repository.update(updated) }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
