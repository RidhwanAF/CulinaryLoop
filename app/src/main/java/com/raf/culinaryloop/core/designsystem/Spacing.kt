package com.raf.culinaryloop.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 4dp / 8dp spatial layout grid tokens.
 */
@Immutable
data class CulinarySpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val m: Dp = 12.dp,
    val l: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp
)

val LocalSpacing = staticCompositionLocalOf { CulinarySpacing() }

/**
 * Corner radius shapes and elevation design tokens.
 */
object CulinaryDimensions {
    val RadiusSmall: Dp = 8.dp
    val RadiusMedium: Dp = 12.dp
    val RadiusLarge: Dp = 16.dp
    val RadiusExtraLarge: Dp = 24.dp
    val RadiusPill: Dp = 999.dp

    val ElevationNone: Dp = 0.dp
    val ElevationLow: Dp = 1.dp
    val ElevationMedium: Dp = 3.dp
    val ElevationHigh: Dp = 6.dp
    val ElevationDialog: Dp = 12.dp

    val MinTouchTarget: Dp = 48.dp
    val HeroImageHeight: Dp = 280.dp
    val ThumbnailSize: Dp = 88.dp
    val StepperButtonSize: Dp = 36.dp
}
