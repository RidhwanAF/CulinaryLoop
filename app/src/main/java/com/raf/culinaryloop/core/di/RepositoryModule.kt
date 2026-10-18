package com.raf.culinaryloop.core.di

import com.raf.culinaryloop.data.datasource.EsbRemoteDataSource
import com.raf.culinaryloop.data.datasource.EsbRemoteDataSourceImpl
import com.raf.culinaryloop.data.repository.CartRepositoryImpl
import com.raf.culinaryloop.data.repository.MenuRepositoryImpl
import com.raf.culinaryloop.data.repository.OrderRepositoryImpl
import com.raf.culinaryloop.data.repository.SettingsRepositoryImpl
import com.raf.culinaryloop.data.repository.StoreRepositoryImpl
import com.raf.culinaryloop.domain.repository.CartRepository
import com.raf.culinaryloop.domain.repository.MenuRepository
import com.raf.culinaryloop.domain.repository.OrderRepository
import com.raf.culinaryloop.domain.repository.SettingsRepository
import com.raf.culinaryloop.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module binding domain repository contracts to concrete data implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository

    @Binds
    @Singleton
    abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindEsbRemoteDataSource(impl: EsbRemoteDataSourceImpl): EsbRemoteDataSource
}
