package com.raf.culinaryloop.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.CulinaryColors
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback

/**
 * Custom MapLibre map marker callout with animated expanded metadata card on selection.
 *
 * @param storeName Name of the outlet branch.
 * @param isOpen Whether the store is currently operating.
 * @param isSelected Whether this marker is actively selected.
 * @param onClick Triggered when the marker pin is tapped.
 * @param modifier Composable modifier.
 */
@Composable
fun MapMarkerCallout(
    storeName: String,
    isOpen: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalAppHapticFeedback.current

    Column(
        modifier = modifier.clickable {
            haptics.performPinSelect()
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = isSelected) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(CulinaryDimensions.RadiusSmall)),
                shape = RoundedCornerShape(CulinaryDimensions.RadiusSmall),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isOpen) CulinaryColors.StatusOpen else CulinaryColors.StatusClosed)
                    )
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }

        // Circular Map Pin
        Box(
            modifier = Modifier
                .size(if (isSelected) 44.dp else 36.dp)
                .shadow(
                    elevation = if (isSelected) 8.dp else 4.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .border(
                    width = 2.5.dp,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = stringResource(R.string.cd_store_marker),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(if (isSelected) 22.dp else 18.dp)
            )
        }
    }
}
