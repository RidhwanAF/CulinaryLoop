package com.raf.culinaryloop.domain.model

/**
 * Strongly-typed menu item identifier.
 */
@JvmInline
value class MenuItemId(val value: String)

/**
 * Strongly-typed menu category identifier.
 */
@JvmInline
value class CategoryId(val value: String)

/**
 * Menu category entity for organizing food and beverage catalog.
 */
data class MenuCategory(
    val id: CategoryId,
    val name: String,
    val displayOrder: Int
)

/**
 * Individual modifier choice (e.g., "70% Less Sugar", "Boba Pearls").
 */
data class ModifierOption(
    val id: String,
    val name: String,
    val priceDelta: Long
)

/**
 * Group of customizable options for a menu item.
 */
data class ModifierGroup(
    val id: String,
    val title: String,
    val isRequired: Boolean,
    val minSelections: Int,
    val maxSelections: Int,
    val options: List<ModifierOption>
)

/**
 * Business entity representing an orderable food or beverage item.
 */
data class MenuItem(
    val id: MenuItemId,
    val categoryId: CategoryId,
    val name: String,
    val description: String,
    val basePrice: Long,
    val imageUrl: String,
    val isAvailable: Boolean,
    val modifierGroups: List<ModifierGroup>
)
