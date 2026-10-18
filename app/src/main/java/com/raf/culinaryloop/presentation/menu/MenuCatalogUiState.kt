package com.raf.culinaryloop.presentation.menu

import androidx.compose.runtime.Immutable
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Immutable UI State representation for Menu Catalog screen.
 */
@Immutable
data class MenuCatalogUiState(
    val categories: ImmutableList<MenuCategory> = persistentListOf(),
    val selectedCategoryId: CategoryId? = null,
    val menuItems: ImmutableList<MenuItem> = persistentListOf(),
    val activeStore: Store? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val cartItemCount: Int = 0,
    val cartGrandTotal: Long = 0L
) {
    val filteredMenuItems: List<MenuItem>
        get() = if (searchQuery.isBlank()) menuItems
        else menuItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
}
