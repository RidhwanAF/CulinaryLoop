package com.raf.culinaryloop.core.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.raf.culinaryloop.domain.model.ThemeMode

/**
 * CompositionLocal exposing whether the active application theme is dark.
 */
val LocalThemeIsDark = compositionLocalOf { false }

/**
 * Root Material 3 theme composable for CulinaryLoop.
 *
 * @param themeMode Explicit theme mode ([ThemeMode.FollowSystem], [ThemeMode.Light], [ThemeMode.Dark]).
 * @param dynamicColor Toggles Dynamic Material You color scheme on Android 12+ (API 31+).
 * @param isHapticEnabled Toggles sensory tactile feedback throughout the design system.
 * @param content Screen layout hierarchy.
 */
@Composable
fun CulinaryLoopTheme(
    themeMode: ThemeMode = ThemeMode.FollowSystem,
    dynamicColor: Boolean = false,
    isHapticEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.FollowSystem -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val context = LocalContext.current
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val hapticFeedback = rememberAppHapticFeedback(enabled = isHapticEnabled)

    CompositionLocalProvider(
        LocalSpacing provides CulinarySpacing(),
        LocalAppHapticFeedback provides hapticFeedback,
        LocalThemeIsDark provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CulinaryTypography,
            content = content
        )
    }
}
