package com.raf.culinaryloop.core.di

import android.content.Context
import com.raf.culinaryloop.core.common.DefaultDispatcherProvider
import com.raf.culinaryloop.core.common.DispatcherProvider
import com.raf.culinaryloop.core.datastore.UserPreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module providing persistence and thread dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideUserPreferencesDataSource(
        @ApplicationContext context: Context
    ): UserPreferencesDataSource = UserPreferencesDataSource(context)
}
