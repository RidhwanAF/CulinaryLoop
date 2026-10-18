package com.raf.culinaryloop.domain.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.model.StoreId
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary for discovering and selecting restaurant outlets.
 */
interface StoreRepository {
    /**
     * Observes real-time or cached store branches.
     */
    fun observeStores(): Flow<AppResult<List<Store>, DomainError>>

    /**
     * Fetches current outlet branches from remote/cache.
     */
    suspend fun getStores(): AppResult<List<Store>, DomainError>

    /**
     * Retrieves specific outlet metadata by [storeId].
     */
    suspend fun getStoreById(storeId: StoreId): AppResult<Store, DomainError>

    /**
     * Observes the currently selected active store for ordering.
     */
    fun observeSelectedStore(): Flow<Store?>

    /**
     * Updates the active store.
     */
    suspend fun selectStore(store: Store)
}
