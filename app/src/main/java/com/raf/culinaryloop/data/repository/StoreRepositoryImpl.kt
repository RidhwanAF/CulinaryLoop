package com.raf.culinaryloop.data.repository

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DispatcherProvider
import com.raf.culinaryloop.core.common.DomainError
import com.raf.culinaryloop.core.common.map
import com.raf.culinaryloop.data.datasource.EsbRemoteDataSource
import com.raf.culinaryloop.data.model.toDomain
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.model.StoreId
import com.raf.culinaryloop.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [StoreRepository] coordinating ESB remote branches and cache.
 */
@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val remoteDataSource: EsbRemoteDataSource,
    private val dispatchers: DispatcherProvider
) : StoreRepository {

    private val selectedStoreFlow = MutableStateFlow<Store?>(null)

    override fun observeStores(): Flow<AppResult<List<Store>, DomainError>> = flow {
        emit(getStores())
    }.flowOn(dispatchers.io)

    override suspend fun getStores(): AppResult<List<Store>, DomainError> = withContext(dispatchers.io) {
        val result = remoteDataSource.getBranches().map { dtos ->
            dtos.map { it.toDomain() }
        }
        if (result is AppResult.Success<List<Store>> && selectedStoreFlow.value == null && result.data.isNotEmpty()) {
            selectedStoreFlow.value = result.data.first()
        }
        result
    }

    override suspend fun getStoreById(storeId: StoreId): AppResult<Store, DomainError> =
        withContext(dispatchers.io) {
            remoteDataSource.getBranchById(storeId.value).map { it.toDomain() }
        }

    override fun observeSelectedStore(): Flow<Store?> = selectedStoreFlow.asStateFlow()

    override suspend fun selectStore(store: Store) {
        selectedStoreFlow.value = store
    }
}
