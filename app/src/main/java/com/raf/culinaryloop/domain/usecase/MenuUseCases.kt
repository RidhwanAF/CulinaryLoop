package com.raf.culinaryloop.domain.usecase

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.MenuItemId
import com.raf.culinaryloop.domain.model.StoreId
import com.raf.culinaryloop.domain.repository.MenuRepository
import javax.inject.Inject

/**
 * UC-02: Retrieves category grouping list for an outlet.
 */
class GetCategoriesUseCase @Inject constructor(
    private val repository: MenuRepository
) {
    suspend operator fun invoke(storeId: StoreId): AppResult<List<MenuCategory>, DomainError> =
        repository.getCategories(storeId)
}

/**
 * UC-02: Retrieves menu items for catalog display.
 */
class GetMenuItemsUseCase @Inject constructor(
    private val repository: MenuRepository
) {
    suspend operator fun invoke(
        storeId: StoreId,
        categoryId: CategoryId? = null
    ): AppResult<List<MenuItem>, DomainError> = repository.getMenuItems(storeId, categoryId)
}

/**
 * UC-02: Retrieves individual product detail and modifier options.
 */
class GetMenuItemDetailUseCase @Inject constructor(
    private val repository: MenuRepository
) {
    suspend operator fun invoke(itemId: MenuItemId): AppResult<MenuItem, DomainError> =
        repository.getMenuItemById(itemId)
}
