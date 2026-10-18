package com.raf.culinaryloop.domain.usecase

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.model.StoreId
import com.raf.culinaryloop.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UC-01: Observes available restaurant outlet branches.
 */
class ObserveStoresUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(): Flow<AppResult<List<Store>, DomainError>> = repository.observeStores()
}

/**
 * UC-01: Retrieves restaurant outlet branches for location filtering.
 */
class GetStoresUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(): AppResult<List<Store>, DomainError> = repository.getStores()
}

/**
 * UC-01: Retrieves a single outlet by identifier.
 */
class GetStoreByIdUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(storeId: StoreId): AppResult<Store, DomainError> = repository.getStoreById(storeId)
}

/**
 * UC-01: Selects an outlet as active for menu ordering.
 */
class SelectStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(store: Store) = repository.selectStore(store)
}

/**
 * UC-01: Observes the currently selected outlet.
 */
class ObserveSelectedStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(): Flow<Store?> = repository.observeSelectedStore()
}
