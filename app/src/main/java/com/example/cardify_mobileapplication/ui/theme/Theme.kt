package com.example.cardify_mobileapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NeoColorScheme = lightColorScheme(
    primary = NeoBlack,
    secondary = NeoGreen,
    tertiary = NeoBlue,
    background = NeoLightGray,
    surface = NeoWhite,
    onPrimary = NeoWhite,
    onSecondary = NeoBlack,
    onTertiary = NeoWhite,
    onBackground = NeoBlack,
    onSurface = NeoBlack,
    error = NeoRed,
    onError = NeoWhite
)

@Composable
fun Cardify_MobileApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Ignored for Neo-Brutalism
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = NeoColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}