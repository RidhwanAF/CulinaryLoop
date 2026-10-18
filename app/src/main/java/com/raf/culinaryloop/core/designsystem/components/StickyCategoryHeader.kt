package com.raf.culinaryloop.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raf.culinaryloop.core.designsystem.CulinaryDimensions
import com.raf.culinaryloop.core.designsystem.LocalAppHapticFeedback
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory

/**
 * Pinned category navigation header supporting fast category filter switching with tactile feedback.
 *
 * @param categories List of menu categories.
 * @param selectedCategoryId Currently active category filter or null for all.
 * @param onSelectCategory Invoked with newly chosen category or null.
 * @param modifier Composable modifier.
 */
@Composable
fun StickyCategoryHeader(
    categories: List<MenuCategory>,
    selectedCategoryId: CategoryId?,
    onSelectCategory: (CategoryId?) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalAppHapticFeedback.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = CulinaryDimensions.ElevationLow
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "all_categories") {
                val isSelected = selectedCategoryId == null
                CategoryTabPill(
                    name = "All Items",
                    isSelected = isSelected,
                    onClick = {
                        haptics.performClick()
                        onSelectCategory(null)
                    }
                )
            }

            items(categories, key = { it.id.value }) { category ->
                val isSelected = selectedCategoryId == category.id
                CategoryTabPill(
                    name = category.name,
                    isSelected = isSelected,
                    onClick = {
                        haptics.performClick()
                        onSelectCategory(category.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryTabPill(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(CulinaryDimensions.RadiusPill)
    val background = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(shape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
