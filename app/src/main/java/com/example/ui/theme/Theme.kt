package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = JsvGold,
    onPrimary = JsvOnPrimary,
    primaryContainer = JsvGoldDark,
    onPrimaryContainer = JsvTextWhite,
    secondary = JsvBlue,
    onSecondary = JsvOnPrimary,
    secondaryContainer = JsvSurfaceLight,
    onSecondaryContainer = JsvTextWhite,
    tertiary = JsvGreen,
    onTertiary = JsvDarker,
    background = JsvDarker,
    onBackground = JsvTextWhite,
    surface = JsvSurface,
    onSurface = JsvTextWhite,
    surfaceVariant = JsvDarkElevated,
    onSurfaceVariant = JsvTextBody,
    outline = JsvBorder,
    error = JsvRed,
    onError = JsvTextWhite
)

private val LightColorScheme = darkColorScheme(
    primary = JsvGold,
    onPrimary = JsvOnPrimary,
    primaryContainer = JsvGoldDark,
    onPrimaryContainer = JsvTextWhite,
    secondary = JsvBlue,
    onSecondary = JsvOnPrimary,
    background = JsvDarker,
    onBackground = JsvTextWhite,
    surface = JsvSurface,
    onSurface = JsvTextWhite,
    outline = JsvBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep signature JSV branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = JsvDarker.toArgb()
            window.navigationBarColor = JsvDarker.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
