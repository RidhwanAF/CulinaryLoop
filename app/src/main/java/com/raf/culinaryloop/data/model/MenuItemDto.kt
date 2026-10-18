package com.raf.culinaryloop.data.model

import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.MenuItemId
import com.raf.culinaryloop.domain.model.ModifierGroup
import com.raf.culinaryloop.domain.model.ModifierOption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ESB Category DTO.
 */
@Serializable
data class MenuCategoryDto(
    @SerialName("category_id") val categoryId: String,
    @SerialName("name") val name: String,
    @SerialName("display_order") val displayOrder: Int
)

fun MenuCategoryDto.toDomain(): MenuCategory =
    MenuCategory(id = CategoryId(categoryId), name = name, displayOrder = displayOrder)

/**
 * ESB Modifier Option DTO.
 */
@Serializable
data class ModifierOptionDto(
    @SerialName("option_id") val optionId: String,
    @SerialName("option_name") val optionName: String,
    @SerialName("price_delta") val priceDelta: Long
)

fun ModifierOptionDto.toDomain(): ModifierOption =
    ModifierOption(id = optionId, name = optionName, priceDelta = priceDelta)

/**
 * ESB Modifier Group DTO.
 */
@Serializable
data class ModifierGroupDto(
    @SerialName("group_id") val groupId: String,
    @SerialName("title") val title: String,
    @SerialName("is_required") val isRequired: Boolean,
    @SerialName("min_selections") val minSelections: Int,
    @SerialName("max_selections") val maxSelections: Int,
    @SerialName("options") val options: List<ModifierOptionDto>
)

fun ModifierGroupDto.toDomain(): ModifierGroup = ModifierGroup(
    id = groupId,
    title = title,
    isRequired = isRequired,
    minSelections = minSelections,
    maxSelections = maxSelections,
    options = options.map { it.toDomain() }
)

/**
 * ESB Menu Item DTO.
 */
@Serializable
data class MenuItemDto(
    @SerialName("item_id") val itemId: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("base_price") val basePrice: Long,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("is_available") val isAvailable: Boolean,
    @SerialName("modifier_groups") val modifierGroups: List<ModifierGroupDto> = emptyList()
)

fun MenuItemDto.toDomain(): MenuItem = MenuItem(
    id = MenuItemId(itemId),
    categoryId = CategoryId(categoryId),
    name = name,
    description = description,
    basePrice = basePrice,
    imageUrl = imageUrl,
    isAvailable = isAvailable,
    modifierGroups = modifierGroups.map { it.toDomain() }
)
