package com.iqoo.guardian.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GuardianColorScheme = darkColorScheme(
    primary = GAccent,
    onPrimary = GBackground,
    primaryContainer = GCardElevated,
    onPrimaryContainer = GTextPrimary,
    secondary = GTextSecondary,
    onSecondary = GBackground,
    background = GBackground,
    onBackground = GTextPrimary,
    surface = GCard,
    onSurface = GTextPrimary,
    surfaceVariant = GCardElevated,
    onSurfaceVariant = GTextSecondary,
    error = GCritical,
    onError = GTextPrimary,
    outline = GBorderStrong,
    outlineVariant = GBorder
)

/**
 * Guardian is a dark-only surface for the hackathon demo, so the system light
 * theme is intentionally ignored.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun IqooGuardianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = GuardianColorScheme,
        typography = GuardianTypography,
        shapes = GuardianShapes,
        content = content
    )
}
