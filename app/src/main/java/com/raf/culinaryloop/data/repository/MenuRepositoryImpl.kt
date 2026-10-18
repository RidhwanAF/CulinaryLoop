package com.raf.culinaryloop.data.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DispatcherProvider
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.core.common.map
import com.raf.culinaryloop.data.datasource.EsbRemoteDataSource
import com.raf.culinaryloop.data.model.toDomain
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.MenuCategory
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.MenuItemId
import com.raf.culinaryloop.domain.model.StoreId
import com.raf.culinaryloop.domain.repository.MenuRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [MenuRepository] querying ESB product endpoints.
 */
@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val remoteDataSource: EsbRemoteDataSource,
    private val dispatchers: DispatcherProvider
) : MenuRepository {

    override suspend fun getCategories(storeId: StoreId): AppResult<List<MenuCategory>, DomainError> =
        withContext(dispatchers.io) {
            remoteDataSource.getCategories(storeId.value).map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun getMenuItems(
        storeId: StoreId,
        categoryId: CategoryId?
    ): AppResult<List<MenuItem>, DomainError> = withContext(dispatchers.io) {
        remoteDataSource.getMenuItems(storeId.value, categoryId?.value).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getMenuItemById(itemId: MenuItemId): AppResult<MenuItem, DomainError> =
        withContext(dispatchers.io) {
            remoteDataSource.getMenuItemById(itemId.value).map { it.toDomain() }
        }
}
