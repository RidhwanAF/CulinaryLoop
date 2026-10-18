package com.raf.culinaryloop.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * CulinaryLoop Brand & Semantic Tokens
 * Inspired by artisanal espresso, roasted coffee crema, warm terracotta, and fresh matcha.
 */
object CulinaryColors {
    // Primary - Deep Espresso Roast & Terracotta Amber
    val AmberOrangePrimary = Color(0xFFC85A17)
    val AmberOrangeOnPrimary = Color(0xFFFFFFFF)
    val AmberOrangeContainer = Color(0xFFFFDBCF)
    val AmberOrangeOnContainer = Color(0xFF381400)

    // Secondary - Warm Latte Crema
    val CremaSecondary = Color(0xFF765849)
    val CremaOnSecondary = Color(0xFFFFFFFF)
    val CremaSecondaryContainer = Color(0xFFFFDBCF)
    val CremaOnSecondaryContainer = Color(0xFF2C160B)

    // Tertiary - Matcha Leaf Sage
    val MatchaTertiary = Color(0xFF55643B)
    val MatchaOnTertiary = Color(0xFFFFFFFF)
    val MatchaTertiaryContainer = Color(0xFFD8EAA5)
    val MatchaOnTertiaryContainer = Color(0xFF141F02)

    // Neutral Surfaces - Warm Bakery Porcelain (Light)
    val SurfaceLight = Color(0xFFFCF9F7)
    val OnSurfaceLight = Color(0xFF201A18)
    val SurfaceVariantLight = Color(0xFFF4DED5)
    val OnSurfaceVariantLight = Color(0xFF52443D)
    val OutlineLight = Color(0xFF85736C)
    val OutlineVariantLight = Color(0xFFD7C2B9)

    // Neutral Surfaces - Dark Roast Espresso (Dark)
    val SurfaceDark = Color(0xFF181210)
    val OnSurfaceDark = Color(0xFFEDE0DC)
    val SurfaceVariantDark = Color(0xFF52443D)
    val OnSurfaceVariantDark = Color(0xFFD7C2B9)
    val OutlineDark = Color(0xFFA08D85)
    val OutlineVariantDark = Color(0xFF52443D)

    // Status / Accent Tokens
    val ErrorLight = Color(0xFFBA1A1A)
    val ErrorContainerLight = Color(0xFFFFDAD6)
    val OnErrorContainerLight = Color(0xFF410002)

    val ErrorDark = Color(0xFFFFB4AB)
    val ErrorContainerDark = Color(0xFF93000A)
    val OnErrorContainerDark = Color(0xFFFFDAD6)

    val StatusOpen = Color(0xFF2E7D32)
    val StatusClosed = Color(0xFFC62828)
    val StarGold = Color(0xFFF9A825)
}

val LightColorScheme = lightColorScheme(
    primary = CulinaryColors.AmberOrangePrimary,
    onPrimary = CulinaryColors.AmberOrangeOnPrimary,
    primaryContainer = CulinaryColors.AmberOrangeContainer,
    onPrimaryContainer = CulinaryColors.AmberOrangeOnContainer,
    secondary = CulinaryColors.CremaSecondary,
    onSecondary = CulinaryColors.CremaOnSecondary,
    secondaryContainer = CulinaryColors.CremaSecondaryContainer,
    onSecondaryContainer = CulinaryColors.CremaOnSecondaryContainer,
    tertiary = CulinaryColors.MatchaTertiary,
    onTertiary = CulinaryColors.MatchaOnTertiary,
    tertiaryContainer = CulinaryColors.MatchaTertiaryContainer,
    onTertiaryContainer = CulinaryColors.MatchaOnTertiaryContainer,
    background = CulinaryColors.SurfaceLight,
    onBackground = CulinaryColors.OnSurfaceLight,
    surface = CulinaryColors.SurfaceLight,
    onSurface = CulinaryColors.OnSurfaceLight,
    surfaceVariant = CulinaryColors.SurfaceVariantLight,
    onSurfaceVariant = CulinaryColors.OnSurfaceVariantLight,
    outline = CulinaryColors.OutlineLight,
    outlineVariant = CulinaryColors.OutlineVariantLight,
    error = CulinaryColors.ErrorLight,
    errorContainer = CulinaryColors.ErrorContainerLight,
    onError = Color.White,
    onErrorContainer = CulinaryColors.OnErrorContainerLight
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB596),
    onPrimary = Color(0xFF581E00),
    primaryContainer = Color(0xFF7A2E00),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFE6BEAB),
    onSecondary = Color(0xFF432B1E),
    secondaryContainer = Color(0xFF5C4133),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFFBCCE8C),
    onTertiary = Color(0xFF283511),
    tertiaryContainer = Color(0xFF3E4C25),
    onTertiaryContainer = Color(0xFFD8EAA5),
    background = CulinaryColors.SurfaceDark,
    onBackground = CulinaryColors.OnSurfaceDark,
    surface = CulinaryColors.SurfaceDark,
    onSurface = CulinaryColors.OnSurfaceDark,
    surfaceVariant = CulinaryColors.SurfaceVariantDark,
    onSurfaceVariant = CulinaryColors.OnSurfaceVariantDark,
    outline = CulinaryColors.OutlineDark,
    outlineVariant = CulinaryColors.OutlineVariantDark,
    error = CulinaryColors.ErrorDark,
    errorContainer = CulinaryColors.ErrorContainerDark,
    onError = Color(0xFF690005),
    onErrorContainer = CulinaryColors.OnErrorContainerDark
)
