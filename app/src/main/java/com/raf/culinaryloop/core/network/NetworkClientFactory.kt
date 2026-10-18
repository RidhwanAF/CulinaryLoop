package com.raf.culinaryloop.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

/**
 * Factory constructing OkHttp and Retrofit instances configured for ESB API.
 */
object NetworkClientFactory {

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
    }

    fun createOkHttpClient(
        authInterceptor: EsbAuthInterceptor = EsbAuthInterceptor()
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .readTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .writeTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    fun createEsbApiService(
        okHttpClient: OkHttpClient = createOkHttpClient(),
        baseUrl: String = EsbApiConfig.BASE_URL
    ): EsbApiService {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(EsbApiService::class.java)
    }
}
