package com.raf.culinaryloop.core.di

import com.raf.culinaryloop.core.network.EsbApiService
import com.raf.culinaryloop.core.network.EsbAuthInterceptor
import com.raf.culinaryloop.core.network.NetworkClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Dagger Hilt module configuring Retrofit, OkHttp, and ESB API network singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideEsbAuthInterceptor(): EsbAuthInterceptor = EsbAuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: EsbAuthInterceptor): OkHttpClient =
        NetworkClientFactory.createOkHttpClient(authInterceptor)

    @Provides
    @Singleton
    fun provideEsbApiService(okHttpClient: OkHttpClient): EsbApiService =
        NetworkClientFactory.createEsbApiService(okHttpClient)
}
