package com.raf.culinaryloop.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback

/**
 * Standard elevated card with tactile click feedback, Material 3 tonal elevation, and border stroke.
 *
 * @param modifier Layout modifier.
 * @param onClick Optional tap callback triggering haptics.
 * @param border Optional custom border stroke.
 * @param content Inner composable slot.
 */
@Composable
fun CulinaryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val haptics = LocalAppHapticFeedback.current
    val shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium)

    val clickModifier = if (onClick != null) {
        Modifier
            .clip(shape)
            .clickable {
                haptics.performClick()
                onClick()
            }
    } else Modifier

    Card(
        modifier = modifier.then(clickModifier),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = CulinaryDimensions.ElevationLow,
            pressedElevation = CulinaryDimensions.ElevationMedium
        ),
        border = border ?: BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(content = content)
    }
}
