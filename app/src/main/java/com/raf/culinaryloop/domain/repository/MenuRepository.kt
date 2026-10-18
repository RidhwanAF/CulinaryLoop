package com.raf.culinaryloop.domain.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.MenuItemId
import com.raf.culinaryloop.domain.model.StoreId

/**
 * Domain boundary for retrieving food and beverage catalog data.
 */
interface MenuRepository {
    /**
     * Retrieves all categories available for a given [storeId].
     */
    suspend fun getCategories(storeId: StoreId): AppResult<List<MenuCategory>, DomainError>

    /**
     * Retrieves menu items for a store, optionally filtered by [categoryId].
     */
    suspend fun getMenuItems(
        storeId: StoreId,
        categoryId: CategoryId? = null
    ): AppResult<List<MenuItem>, DomainError>

    /**
     * Retrieves specific product details including modifier groups by [itemId].
     */
    suspend fun getMenuItemById(itemId: MenuItemId): AppResult<MenuItem, DomainError>
}
