package com.raf.culinaryloop.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback

/**
 * Visual variant styles for [CulinaryButton].
 */
enum class CulinaryButtonVariant {
    Primary,
    Outlined,
    Tonal
}

/**
 * Standard tactile action button with embedded loading spinner and haptic click.
 *
 * @param text Button label.
 * @param onClick Triggered on tap.
 * @param modifier Composable modifier.
 * @param variant Visual button styling.
 * @param enabled Whether button is interactable.
 * @param isLoading Whether loading indicator is spinning.
 */
@Composable
fun CulinaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CulinaryButtonVariant = CulinaryButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val haptics = LocalAppHapticFeedback.current
    val shape = RoundedCornerShape(CulinaryDimensions.RadiusMedium)

    val onAction = {
        if (!isLoading && enabled) {
            haptics.performClick()
            onClick()
        }
    }

    when (variant) {
        CulinaryButtonVariant.Primary -> {
            Button(
                onClick = onAction,
                modifier = modifier
                    .fillMaxWidth()
                    .height(CulinaryDimensions.MinTouchTarget),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.onPrimary)
            }
        }
        CulinaryButtonVariant.Outlined -> {
            OutlinedButton(
                onClick = onAction,
                modifier = modifier
                    .fillMaxWidth()
                    .height(CulinaryDimensions.MinTouchTarget),
                enabled = enabled && !isLoading,
                shape = shape,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.primary)
            }
        }
        CulinaryButtonVariant.Tonal -> {
            Button(
                onClick = onAction,
                modifier = modifier
                    .fillMaxWidth()
                    .height(CulinaryDimensions.MinTouchTarget),
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.filledTonalButtonColors(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                ButtonContent(text = text, isLoading = isLoading, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    contentColor: Color
) {
    Box(contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
