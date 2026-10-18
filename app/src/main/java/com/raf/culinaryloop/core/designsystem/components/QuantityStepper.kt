package com.raf.culinaryloop.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.R
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback

/**
 * Stepper control for adjusting cart line item quantity with bound enforcement and tactile feedback.
 *
 * @param quantity Current numeric value.
 * @param onQuantityChange Invoked with newly adjusted quantity.
 * @param modifier Composable modifier.
 * @param minQuantity Minimum threshold (default 1).
 * @param maxQuantity Maximum threshold (default 99).
 */
@Composable
fun QuantityStepper(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    val haptics = LocalAppHapticFeedback.current

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(CulinaryDimensions.RadiusPill)
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(CulinaryDimensions.RadiusPill)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val canDecrease = quantity > minQuantity
        Box(
            modifier = Modifier
                .size(CulinaryDimensions.StepperButtonSize)
                .clip(CircleShape)
                .clickable(enabled = canDecrease) {
                    haptics.performQuantityChange()
                    onQuantityChange(quantity - 1)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.cd_decrease_qty),
                tint = if (canDecrease) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(min = 32.dp)
                .padding(horizontal = 4.dp)
        )

        val canIncrease = quantity < maxQuantity
        Box(
            modifier = Modifier
                .size(CulinaryDimensions.StepperButtonSize)
                .clip(CircleShape)
                .clickable(enabled = canIncrease) {
                    haptics.performQuantityChange()
                    onQuantityChange(quantity + 1)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_increase_qty),
                tint = if (canIncrease) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
